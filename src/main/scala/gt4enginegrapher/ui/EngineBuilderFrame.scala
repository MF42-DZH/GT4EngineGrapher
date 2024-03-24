package gt4enginegrapher.ui

import java.awt.{List => _, _}
import java.awt.event.{ItemEvent, MouseEvent, MouseListener}

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

  setTitle("GT4 Engine Charter")
  setLayout(new BoxLayout(getContentPane, BoxLayout.PAGE_AXIS))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  private val customizerHome = new JPanel()

  // Car selector
  private val carSelector = new JComboBox[SimpleName](allNames.sortBy(_.name).toArray)
  private val oilQualityTick = new JCheckBox("New Oil?")
  add(new JPanel() { inner =>
    setLayout(new BoxLayout(inner, BoxLayout.LINE_AXIS))
    add(new JLabel("Select Car: "))
    add(new JPanel() { split =>
      setLayout(new BoxLayout(split, BoxLayout.LINE_AXIS)); add(carSelector); add(oilQualityTick)
    })
  })
  add(customizerHome)

  carSelector.addItemListener((e: ItemEvent) => {
    if (e.getStateChange == ItemEvent.SELECTED) {
      customizerHome.removeAll()
      customizerHome.add(newCustomizer(carSelector.getSelectedItem.asInstanceOf[SimpleName]))
      ebf.pack()
      ebf.repaint()
    }
  })

  // XXX: Workaround to force the initial set of settings to show up.
  carSelector.setSelectedIndex(1)
  carSelector.setSelectedIndex(0)

  // Car customizer
  private def newCustomizer(name: SimpleName): JPanel = new JPanel() { inner =>
    setLayout(new GridLayout(3, 4, 2, 2))

    def byLabel[T <: SpecTable[_]](table: TableQuery[T]) =
      db.run {
        table
          .filter(_.label.like(s"%${name.label}%"))
          .result
      }

    def generateCustomizer[T <: Object: ClassTag](
      label: String,
    )(items: => Seq[T]): (JComboBox[T], JPanel) = {
      val selector = new JComboBox[T](items.toArray)
      val panel = new JPanel() { selectorPanel =>
        setLayout(new GridLayout(0, 1, 2, 0))
        add(new JLabel(label))
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
      ) +: Await.result(byLabel(portPolishes).map(_.map(_.asInstanceOf[PortPolish])), Duration.Inf)
    }
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
      ) +: Await.result(
        byLabel(engineBalances).map(_.map(_.asInstanceOf[EngineBalance])),
        Duration.Inf,
      )
    }
    val (dus, dup) = generateCustomizer[DisplacementUp]("Displacement Up") {
      DisplacementUp(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: Await.result(
        byLabel(displacementUps).map(_.map(_.asInstanceOf[DisplacementUp])),
        Duration.Inf,
      )
    }
    val (exs, exp) = generateCustomizer[Muffler]("Exhaust") {
      Muffler(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: Await
        .result(byLabel(mufflers).map(_.map(_.asInstanceOf[Muffler])), Duration.Inf)
        .sortBy(_.category)
    }
    val (ecus, ecup) = generateCustomizer[Computer]("Racing Chip") {
      Computer(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: Await.result(byLabel(computers).map(_.map(_.asInstanceOf[Computer])), Duration.Inf)
    }
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
        ) +: Await
          .result(byLabel(naTunes).map(_.map(_.asInstanceOf[NATune])), Duration.Inf)
          .sortBy(_.category)
      }
    val (tks, tkp) =
      generateCustomizer[TurbineKit]("Turbine Kit") {
        val turbines = Await
          .result(byLabel(turbineKits).map(_.map(_.asInstanceOf[TurbineKit])), Duration.Inf)
          .sortBy(_.category)

        if (turbines.exists(_.category == 0)) turbines
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
    val (ics, icp) = generateCustomizer[Intercooler]("Intercooler") {
      Intercooler(
        rowId                 = 0,
        label                 = "notapplied",
        highRPMTorqueModifier = 100,
        lowRPMTorqueModifier  = 100,
        price                 = 0,
        category              = 0,
      ) +: Await
        .result(byLabel(intercoolers).map(_.map(_.asInstanceOf[Intercooler])), Duration.Inf)
        .sortBy(_.category)
    }
    val (scs, scp) =
      generateCustomizer[Supercharger]("Supercharger") {
        Supercharger(
          rowId                 = 0,
          label                 = "notapplied",
          highRPMTorqueModifier = 100,
          lowRPMTorqueModifier  = 100,
          price                 = 0,
          category              = 0,
        ) +: Await.result(
          byLabel(superchargers).map(_.map(_.asInstanceOf[Supercharger])),
          Duration.Inf,
        )
      }
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
      ) +: Await.result(byLabel(nitrouses).map(_.map(_.asInstanceOf[Nitrous])), Duration.Inf)
    }

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

    noss.addItemListener((e: ItemEvent) => {
      if (e.getStateChange == ItemEvent.SELECTED) {
        val nit = noss.getSelectedItem.asInstanceOf[Nitrous]
        val shouldEnable = nit.label != "notapplied"

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

    // Row 1
    add(ppp)
    add(exp)
    add(tkp)
    add(nosp)

    // Row 2
    add(ebp)
    add(ecup)
    add(icp)
    add(nsp)

    // Row 3
    add(dup)
    add(nap)
    add(scp)
    add(new JButton("Map Engine") {
      addMouseListener(new MouseListener {
        override def mouseClicked(e: MouseEvent): Unit = {
          // Verify NOS.
          val nos = noss.getSelectedItem.asInstanceOf[Nitrous]
          var nosStrength: Option[Int] = None

          if (nos.label != "notapplied") {
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
  }
}
