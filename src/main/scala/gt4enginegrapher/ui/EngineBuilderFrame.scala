package gt4enginegrapher.ui

import java.awt.{List => _, _}
import java.awt.event._
import java.util.concurrent.{ExecutorService, Executors}

import javax.swing._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import scala.util.Try

import gt4enginegrapher.schema._
import gt4enginegrapher.wrappers.EngineBuilder
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

class EngineBuilderFrame(allNames: Seq[SimpleName])(implicit
  schema: AllSchema,
  db: JdbcDatabaseDef,
  ec: ExecutionContext,
) extends JFrame { ebf =>
  import schema._
  private val worker: ExecutorService = Executors.newSingleThreadExecutor()

  setTitle("GT4 Engine Charter")
  setLayout(new BoxLayout(getContentPane, BoxLayout.PAGE_AXIS))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      if (e.getNewState == WindowEvent.WINDOW_CLOSING) {
        worker.shutdownNow()
      }
    }
  })

  private val customizerHome = new JPanel()

  // Car selector
  private val carSelector = new JComboBox[SimpleName](
    (SimpleName(
      label = "___not_a_car",
      name  = "[Select a Car]",
    ) +: allNames.sortBy(_.name)).toArray,
  )
  private val oilQualityTick = new JCheckBox("New Oil?")
  private val hybridTick = new JCheckBox("Allow Hybriding?")
  hybridTick.setEnabled(false)

  add(new JPanel() {
    val usedLayout = new FlowLayout()
    usedLayout.setHgap(4)
    setLayout(usedLayout)

    add {
      val label = new JLabel("Use engine of: ")
      label.setFont(label.getFont.deriveFont(Font.BOLD))
      label
    }
    add(new JPanel() {
      setLayout(usedLayout)
      add(carSelector)
      add(hybridTick)
      add(oilQualityTick)
    })

    setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 2))
  })
  add(customizerHome)

  private def regenerateCustomizer(): Unit = {
    val lbar = new JProgressBar() {
      setString("Loading Available Parts...")
      setStringPainted(true)

      override def getPreferredSize: Dimension = new Dimension(ebf.getWidth - 36, 24)

      pack()
    }
    lbar.setMinimum(0)
    lbar.setMaximum(12)

    val loading = new JPanel() {
      add(lbar)
      pack()
    }

    customizerHome.removeAll()

    if (carSelector.getSelectedItem.asInstanceOf[SimpleName].label != "___not_a_car") {
      customizerHome.add(loading)
      ebf.pack()
      ebf.repaint()

      worker.submit({ () =>
        val customizer = newCustomizer(lbar)
        customizerHome.removeAll()
        customizerHome.add(customizer)
        hybridTick.setEnabled(true)
        ebf.pack()
        ebf.repaint()
      }: Runnable)
    } else {
      hybridTick.setEnabled(false)
      hybridTick.setSelected(false)
      ebf.pack()
      ebf.repaint()
    }
  }

  private var oldItem: Option[SimpleName] = None
  carSelector.addItemListener((e: ItemEvent) => {
    val selected = carSelector.getSelectedItem.asInstanceOf[SimpleName]
    if (
      e.getStateChange == ItemEvent.SELECTED && (!hybridTick.isSelected || selected.label == "___not_a_car") && (oldItem.isEmpty || oldItem.get != selected)
    ) {
      oldItem = Some(selected)
      regenerateCustomizer()
    }
  })

  hybridTick.addItemListener((_: ItemEvent) => regenerateCustomizer())

  // Car customizer
  private def newCustomizer(bar: JProgressBar): JPanel = new JPanel() { inner =>
    private def name = carSelector.getSelectedItem.asInstanceOf[SimpleName]

    val customizerLayout = new FlowLayout()
    customizerLayout.setHgap(8)
    customizerLayout.setVgap(2)

    setLayout(customizerLayout)

    def byLabel[U <: CanHaveCarName, T <: SpecTable[U]](table: TableQuery[T]) =
      db.run {
        table
          .filter(_.label.like(s"%${name.label}%"))
          .result
      }

    def allWithNames[U <: CanHaveCarName, T <: SpecTable[U]](table: TableQuery[T]) =
      db.run {
        names
          .join(table)
          .on { (carName, upgrade) =>
            upgrade.label.like((carName.label.reverseString ++ "%").reverseString ++ "%")
          }
          .result
      }

    def getUpgrades[U <: CanHaveCarName, T <: SpecTable[U]](table: TableQuery[T]): Seq[U] =
      if (hybridTick.isSelected) {
        Await.result(
          allWithNames[U, T](table).map(
            _.map(_.asInstanceOf[(Name, U)])
              .map { case (name, upgrade) => upgrade.withCarName(name.toSimpleName.label) }
              .sortBy(up => (up.carName, up.category)),
          ),
          Duration.Inf,
        )
      } else {
        Await.result(
          byLabel[U, T](table).map(_.map(_.asInstanceOf[U]).sortBy(_.category)),
          Duration.Inf,
        )
      }

    def generateCustomizer[T <: Object: ClassTag](
      label: String,
    )(items: => Seq[T]): (JComboBox[T], JPanel) = {
      val selector = new JComboBox[T](items.toArray)
      val panel = new JPanel() { selectorPanel =>
        setLayout(new GridLayout(0, 1, 2, 0))
        add {
          val heading = new JLabel(label)
          heading.setFont(heading.getFont.deriveFont(Font.BOLD))
          heading
        }
        add(selector)
      }

      (selector, panel)
    }

    val (pps, ppp) = generateCustomizer[PortPolish]("Port Polish") {
      PortPolish(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: getUpgrades[PortPolish, PortPolishT](portPolishes)
    }
    bar.setValue(1)
    val (ebs, ebp) = generateCustomizer[EngineBalance]("Engine Balancing") {
      EngineBalance(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
        shiftLimit            = 0,
        revLimit              = 0,
      ) +: getUpgrades[EngineBalance, EngineBalanceT](engineBalances)
    }
    bar.setValue(2)
    val (dus, dup) = generateCustomizer[DisplacementUp]("Displacement Up") {
      DisplacementUp(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: getUpgrades[DisplacementUp, DisplacementUpT](displacementUps)
    }
    bar.setValue(3)
    val (exs, exp) = generateCustomizer[Muffler]("Exhaust") {
      Muffler(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: getUpgrades[Muffler, MufflerT](mufflers)
    }
    bar.setValue(4)
    val (ecus, ecup) = generateCustomizer[Computer]("Racing Chip") {
      Computer(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: getUpgrades[Computer, ComputerT](computers)
    }
    bar.setValue(5)
    val (nas, nap) =
      generateCustomizer[NATune]("NA Tuning") {
        NATune(
          rowId                 = 0,
          label                 = "notapplied",
          price                 = 0,
          highRPMTorqueModifier = 100,
          lowRPMTorqueModifier  = 100,
          category              = 0,
          shiftLimit            = 0,
          revLimit              = 0,
        ) +: getUpgrades[NATune, NATuneT](naTunes)
      }
    bar.setValue(6)
    val (tks, tkp) =
      generateCustomizer[TurbineKit]("Turbine Kit") {
        val turbines = getUpgrades[TurbineKit, TurbineKitT](turbineKits)

        if (turbines.exists(_.category == 0) && !hybridTick.isSelected) turbines
        else
          TurbineKit(
            rowId                 = 0,
            label                 = "notapplied",
            price                 = 0,
            highRPMTorqueModifier = 100,
            lowRPMTorqueModifier  = 100,
            category              = 0,
            wastegate             = 0,
            boost1                = 0,
            peakRpm1              = 0,
            response1             = 0,
            boost2                = 0,
            peakRpm2              = 0,
            response2             = 0,
            shiftLimit            = 0,
            revLimit              = 0,
          ) +: turbines
      }
    bar.setValue(7)
    val (ics, icp) = generateCustomizer[Intercooler]("Intercooler") {
      Intercooler(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: getUpgrades[Intercooler, IntercoolerT](intercoolers)
    }
    bar.setValue(8)
    val (scs, scp) =
      generateCustomizer[Supercharger]("Supercharger") {
        Supercharger(
          rowId                 = 0,
          label                 = "notapplied",
          highRPMTorqueModifier = 100,
          lowRPMTorqueModifier  = 100,
          price                 = 0,
          category              = 0,
        ) +: getUpgrades[Supercharger, SuperchargerT](superchargers)
      }
    bar.setValue(9)
    val (noss, nosp) = generateCustomizer[Nitrous]("Nitrous") {
      Nitrous(
        rowId          = 0,
        label          = "notapplied",
        _unused        = 0,
        capacity       = 0,
        category       = 0,
        defaultSetting = 0,
        minSetting     = 0,
        maxSetting     = 0,
      ) +: getUpgrades[Nitrous, NitrousT](nitrouses)
    }
    bar.setValue(10)

    // Only allow one aspiration to be active:
    nas.addItemListener((e: ItemEvent) => {
      if (
        e.getStateChange == ItemEvent.SELECTED && nas.getSelectedItem
          .asInstanceOf[NATune]
          .category > 0
      ) {
        tks.setSelectedIndex(0)
        scs.setSelectedIndex(0)
      }
    })

    tks.addItemListener((e: ItemEvent) => {
      if (
        e.getStateChange == ItemEvent.SELECTED && tks.getSelectedItem
          .asInstanceOf[TurbineKit]
          .category > 0
      ) {
        nas.setSelectedIndex(0)
        scs.setSelectedIndex(0)
      }
    })

    scs.addItemListener((e: ItemEvent) => {
      if (
        e.getStateChange == ItemEvent.SELECTED && scs.getSelectedItem
          .asInstanceOf[Supercharger]
          .category > 0
      ) {
        nas.setSelectedIndex(0)
        tks.setSelectedIndex(0)
      }
    })

    // Nitrous strength input.
    val (nsp, nsi, nsl) = {
      val label = new JLabel("Nitrous Strength")
      val input = new JTextField()

      val panel = new JPanel(new GridLayout(0, 1, 2, 0)) {
        add(label)
        add(input)
      }

      input.setEnabled(false)

      (panel, input, label)
    }
    bar.setValue(11)

    noss.addItemListener((e: ItemEvent) => {
      if (e.getStateChange == ItemEvent.SELECTED) {
        val nit = noss.getSelectedItem.asInstanceOf[Nitrous]
        val shouldEnable = nit.category != 0

        nsi.setEnabled(shouldEnable)

        if (shouldEnable) {
          nsl.setText(s"Nitrous Strength (${nit.minSetting}-${nit.maxSetting})")
        } else {
          nsl.setText(s"Nitrous Strength")
        }

        ebf.pack()
        ebf.repaint()
      }
    })

    // Column 1
    add(new JPanel(new GridLayout(0, 1, 0, 2)) {
      add(ppp)
      add(ebp)
      add(dup)
    })

    // Column 2
    add(new JPanel(new GridLayout(0, 1, 0, 2)) {
      add(exp)
      add(ecup)
      add(nap)
    })

    // Column 3
    add(new JPanel(new GridLayout(0, 1, 0, 2)) {
      add(tkp)
      add(icp)
      add(scp)
    })

    // Column 4
    add(new JPanel(new GridLayout(0, 1, 0, 2)) {
      add(nosp)
      add(nsp)
      add(new JButton("Map Engine") {
        addMouseListener(new MouseListener {
          override def mouseClicked(e: MouseEvent): Unit = {
            // Verify NOS.
            val nos = noss.getSelectedItem.asInstanceOf[Nitrous]
            var nosStrength: Option[Int] = None

            if (nos.category != 0) {
              val current = Try(nsi.getText.toInt).getOrElse(-1)
              if (current < nos.minSetting || current > nos.maxSetting) {
                JOptionPane.showMessageDialog(
                  ebf,
                  "Nitrous strength out of range or not a number!",
                  "Invalid Value",
                  JOptionPane.ERROR_MESSAGE,
                )
                return
              }

              nosStrength = Some(current)
            }

            // Build engine.
            val builder = new EngineBuilder(
              name,
              Await.result(
                db.run(
                  engines
                    .filter(_.label.like(s"%${name.label}%"))
                    .result
                    .map(_.head.asInstanceOf[Engine]),
                ),
                Duration.Inf,
              ),
            )

            builder.chosenPolish         = Some(pps.getSelectedItem.asInstanceOf[PortPolish])
            builder.chosenBalance        = Some(ebs.getSelectedItem.asInstanceOf[EngineBalance])
            builder.chosenDisplacment    = Some(dus.getSelectedItem.asInstanceOf[DisplacementUp])
            builder.chosenComputer       = Some(ecus.getSelectedItem.asInstanceOf[Computer])
            builder.chosenNaTune         = Some(nas.getSelectedItem.asInstanceOf[NATune])
            builder.chosenTurbine        = Some(tks.getSelectedItem.asInstanceOf[TurbineKit])
            builder.chosenMuffler        = Some(exs.getSelectedItem.asInstanceOf[Muffler])
            builder.chosenIntercooler    = Some(ics.getSelectedItem.asInstanceOf[Intercooler])
            builder.chosenSupercharger   = Some(scs.getSelectedItem.asInstanceOf[Supercharger])
            builder.chosenNos            = Some(nos)
            builder.chosenNitrousSetting = nosStrength
            builder.withGoodOil          = oilQualityTick.isSelected

            val (_, engine) = builder.buildEngine()
            val chart = EngineGraphPanel(ebf, name, engine)
            chart.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

            chart.pack()
            chart.setLocationRelativeTo(null)
            chart.setVisible(true)
          }

          override def mousePressed(e: MouseEvent): Unit = ()

          override def mouseReleased(e: MouseEvent): Unit = ()

          override def mouseEntered(e: MouseEvent): Unit = ()

          override def mouseExited(e: MouseEvent): Unit = ()
        })
      })
    })
    bar.setValue(12)
  }
}
