package gtenginegrapher.ui

import java.awt.{List => _, _}
import java.awt.event._
import java.io.{PrintWriter, StringWriter}
import java.util.concurrent.{ExecutorService, Executors}

import javax.swing._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

import gtenginegrapher.schema._
import gtenginegrapher.utils._
import gtenginegrapher.wrappers._
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

class EngineBuilderFrame(allNames: Seq[SimpleName])(implicit
  schema: AllSchema,
  db: JdbcDatabaseDef,
  region: Region,
  wear: WearValues,
  ec: ExecutionContext,
  verbose: Boolean,
) extends JFrame
  with SlickEscapes { ebf =>
  import schema._
  private val worker: ExecutorService = Executors.newSingleThreadExecutor()

  setTitle {
    "Gran Turismo Engine Charter - " +
      (schema match {
        case _: GT3AllSchema => "Gran Turismo 3"
        case _: GT4AllSchema => "Gran Turismo 4"
      }) +
      s" - ${region.toString}"
  }

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
  private val listeners = new java.util.ArrayDeque[KeyEventPostProcessor](2)

  private var wearSaveData: Option[(BigDecimal, WearAdjustmentPanel.WearData)] = None
  private var unitSaveData: (TorqueUnits.KeyVal, PowerUnits.KeyVal) =
    (TorqueUnits.Kgfm, PowerUnits.Ps)

  // Car selector
  private val carSelector = new JComboBox[SimpleName](
    (SimpleName(
      label = "___not_a_car",
      name  = "[Select a Car]",
    ) +: allNames.sortBy(_.name.toLowerCase)).toArray,
  )
  private val unitButton = new JButton("Set Units") {
    addMouseListener(new MouseListener {
      override def mouseClicked(e: MouseEvent): Unit = {
        val up = new UnitPanel(
          ebf,
          data => unitSaveData = data,
          unitSaveData,
        )

        up.pack()
        up.setLocationRelativeTo(null)
        up.setResizable(false)
        up.setVisible(true)
      }

      override def mousePressed(e: MouseEvent): Unit = ()
      override def mouseReleased(e: MouseEvent): Unit = ()
      override def mouseEntered(e: MouseEvent): Unit = ()
      override def mouseExited(e: MouseEvent): Unit = ()
    })
  }
  unitButton.setEnabled(false)

  private val wearButton = new JButton("Wear Settings") {
    addMouseListener(new MouseListener {
      override def mouseClicked(e: MouseEvent): Unit = {
        val adj = new WearAdjustmentPanel(
          ebf,
          carSelector.getSelectedItem.asInstanceOf[SimpleName],
          data => { wearSaveData = Some(data) },
          existingData = wearSaveData.map(_._2),
        )

        adj.pack()
        adj.setLocationRelativeTo(null)
        adj.setResizable(false)
        adj.setVisible(true)
      }

      override def mousePressed(e: MouseEvent): Unit = ()
      override def mouseReleased(e: MouseEvent): Unit = ()
      override def mouseEntered(e: MouseEvent): Unit = ()
      override def mouseExited(e: MouseEvent): Unit = ()
    })
  }
  wearButton.setEnabled(false)

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
      add(unitButton)
      add(wearButton)
      add(hybridTick)
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
        wearSaveData = None
        unitButton.setEnabled(true)
        wearButton.setEnabled(true)
        hybridTick.setEnabled(true)
        ebf.pack()
        ebf.repaint()
      }: Runnable)
    } else {
      hybridTick.setEnabled(false)
      hybridTick.setSelected(false)
      unitButton.setEnabled(false)
      wearButton.setEnabled(false)
      ebf.pack()
      ebf.repaint()
    }
  }

  private var oldItem: Option[SimpleName] = None
  carSelector.addItemListener((e: ItemEvent) => {
    val selected = carSelector.getSelectedItem.asInstanceOf[SimpleName]
    if (
      (e.getStateChange == ItemEvent.SELECTED) &&
      (!hybridTick.isSelected || selected.label == "___not_a_car") &&
      (oldItem.isEmpty || oldItem.get != selected)
    ) {
      oldItem = Some(selected)
      regenerateCustomizer()
    }
  })

  hybridTick.addItemListener((_: ItemEvent) => regenerateCustomizer())

  // Car customizer
  private def newCustomizer(bar: JProgressBar): JPanel = new JPanel() { inner =>
    hybridTick.setEnabled(false)

    private def name = carSelector.getSelectedItem.asInstanceOf[SimpleName]

    val customizerLayout = new FlowLayout()
    customizerLayout.setHgap(8)
    customizerLayout.setVgap(2)

    setLayout(customizerLayout)

    def byLabel[U <: CanHaveCarName, T <: SpecTable[U]](
      table: TableQuery[T],
      labelOverride: Option[Rep[String] => Rep[String]],
    ): Future[Seq[U]] =
      db.run {
        table
          .filter(
            _.label.like(
              labelOverride match {
                case Some(f) => f(name.label)
                case None    => s"%\\_${name.label}\\__"
              },
              esc = '\\',
            ),
          )
          .result
          .withStatements(ebf.getClass)
          .withCounting(ebf.getClass)
      }

    def allWithNames[U <: CanHaveCarName, T <: SpecTable[U]](
      table: TableQuery[T],
      labelOverride: Option[Rep[String] => Rep[String]],
    ): Future[Seq[(Name, U)]] =
      db.run {
        names
          .join(table)
          .on { (carName, upgrade) =>
            upgrade.label.like(
              labelOverride match {
                case Some(f) => f(carName.label)
                case None    => LiteralColumn("%\\_") ++ (carName.label ++ "\\__")
              },
              esc = '\\',
            )
          }
          .result
          .withStatements(ebf.getClass)
          .withCounting(ebf.getClass)
      }

    def getUpgrades[U <: CanHaveCarName, T <: SpecTable[U]](
      table: TableQuery[T],
      labelOverride: Option[Rep[String] => Rep[String]] = None,
    ): Seq[U] =
      if (hybridTick.isSelected) {
        allWithNames[U, T](table, labelOverride)
          .map(
            _.map { case (name, upgrade) => upgrade.withCarName(name.toSimpleName.label) }
              .sortBy((up: U) => (up.carName, up.category)),
          )
          .runBlocking
      } else {
        byLabel[U, T](table, labelOverride).map(_.sortBy(_.category)).runBlocking
      }

    def generateCustomizer[T <: Object: ClassTag](
      label: String,
    )(items: => Seq[T]): (JComboBox[T], JPanel) = {
      val selector = new JComboBox[T](items.toArray)
      val panel = new JPanel() {
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
      ) +: getUpgrades[PortPolish, PortPolishTable](portPolishes)
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
      ) +: getUpgrades[EngineBalance, EngineBalanceTable](engineBalances)
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
      ) +: getUpgrades[DisplacementUp, DisplacementUpTable](displacementUps)
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
      ) +: getUpgrades[Muffler, MufflerTable](mufflers)
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
      ) +: getUpgrades[Computer, ComputerTable](computers)
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
        ) +: getUpgrades[NATune, NATuneTable](naTunes)
      }
    bar.setValue(6)
    val (tks, tkp) =
      generateCustomizer[TurbineKit]("Turbine Kit") {
        val turbines = getUpgrades[TurbineKit, TurbineKitTable](turbineKits)

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
      ) +: getUpgrades[Intercooler, IntercoolerTable](intercoolers)
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
        ) +: getUpgrades[Supercharger, SuperchargerTable](
          superchargers,
          // XXX: For some reason, GT4's 350z Special Edition has a supercharger with a malformed Label.
          labelOverride = Some { (label: Rep[String]) => LiteralColumn("%") ++ (label ++ "\\__") },
        )
      }
    bar.setValue(9)
    val (noss, nosp) = generateCustomizer[Nitrous]("Nitrous") {
      Nitrous(
        rowId          = 0,
        label          = "notapplied",
        _unused        = 0,
        capacity       = 0,
        price          = 0,
        category       = 0,
        defaultSetting = 0,
        minSetting     = 0,
        maxSetting     = 0,
      ) +: getUpgrades[Nitrous, NitrousTable](nitrouses)
    }
    bar.setValue(10)

    // Only allow one aspiration to be active, if hybriding is disabled:
    if (!hybridTick.isSelected) {
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
    }

    // Nitrous strength input.
    val (nsp, nsi, nsl) = {
      val label = new JLabel("Nitrous Strength")
      val input = UIUtils.numberOnlyTextField()

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
      add(new JButton("Map Engine") { button =>
        private def showChart(): Unit = {
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
          val builder = Try {
            new EngineBuilder(
              name,
              db.run(
                engines
                  .filter(_.label.like(s"en\\_%${name.label}\\_%", esc = '\\'))
                  .result
                  .withStatements(ebf.getClass)
                  .withCounting(ebf.getClass)
                  .map(_.head),
              ).runBlocking,
            )
          } match {
            case Failure(exc)   =>
              val writer = new StringWriter()
              exc.printStackTrace(new PrintWriter(writer))

              if (verbose) {
                println {
                  s"[${condenseName(button.getClass.getName)}] Exception Caught: ${writer.toString}"
                }
              }

              JOptionPane.showMessageDialog(
                ebf,
                s"Could not form engine from available data, raw exception:\n\n${writer.toString}",
                "Invalid Engine",
                JOptionPane.ERROR_MESSAGE,
              )

              return
            case Success(value) => value
          }

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
          builder.wearMultipliers = wearSaveData.map { case (m, _) => m }.getOrElse(BigDecimal(1))

          val chart = EngineGraphPanel(ebf, name, builder, unitSaveData)
          chart.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

          chart.pack()
          chart.setLocationRelativeTo(null)
          chart.setVisible(true)
        }

        private def showShoppingList(): Unit = {
          def collectUpgrade[U <: Upgrade](cb: JComboBox[U], name: String): Option[(String, U)] =
            Option.when(cb.getSelectedItem.asInstanceOf[U].category > 0)(
              (name, cb.getSelectedItem.asInstanceOf[U]),
            )

          val primaryPowerUpgrade: Seq[(String, Upgrade)] = Seq(
            collectUpgrade[TurbineKit](tks, "Turbo"),
            collectUpgrade[Supercharger](scs, "Supercharger"),
            collectUpgrade[NATune](nas, "NA Tuning"),
          ).flatten

          val supplementaryParts: Seq[(String, Upgrade)] = Seq(
            collectUpgrade[PortPolish](pps, "Port Polish"),
            collectUpgrade[EngineBalance](ebs, "Engine Balancing"),
            collectUpgrade[Muffler](exs, "Exhaust & Air Cleaner"),
            collectUpgrade[Intercooler](ics, "Intercooler"),
            collectUpgrade[Computer](ecus, "Racing Chip"),
            collectUpgrade[DisplacementUp](dus, "Displacement Up"),
            collectUpgrade[Nitrous](noss, "Nitrous"),
          ).flatten

          val shoppingList = new ShoppingList(
            ebf,
            Map(
              "Primary Power Part"  -> primaryPowerUpgrade,
              "Supplementary Parts" -> supplementaryParts,
              "Oil Change"          -> Option
                .when(wearSaveData.exists { case (_, ((otk, otd), (_, _))) => otk && (otd < 300) })(
                  (
                    "New Oil",
                    new HasTorqueRemapping {
                      override def highRPMTorqueModifier: Int = 105
                      override def lowRPMTorqueModifier: Int = 105

                      override val category: Int = 1
                      override val price: Int = schema match {
                        case _: GT3AllSchema => 250
                        case _: GT4AllSchema => 50
                      }

                      override def toString: String = "Applied"
                    },
                  ),
                )
                .toSeq,
            ).filter(_._2.iterator.nonEmpty),
            name,
          )

          shoppingList.pack()
          shoppingList.setLocationRelativeTo(null)
          shoppingList.setResizable(false)
          shoppingList.setVisible(true)
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventPostProcessor {
          val listener = new KeyEventPostProcessor {
            override def postProcessKeyEvent(e: KeyEvent): Boolean = {
              if (e.isShiftDown) button.setText("Get Shopping List")
              else if (!e.isShiftDown) button.setText("Map Engine")

              true
            }
          }

          Option(listeners.poll()) match {
            case Some(old) =>
              KeyboardFocusManager.getCurrentKeyboardFocusManager.removeKeyEventPostProcessor(old)
            case None      => ()
          }

          listeners.add(listener)

          listener
        }

        addMouseListener(new MouseListener {
          override def mouseClicked(e: MouseEvent): Unit =
            if (e.isShiftDown) showShoppingList()
            else showChart()

          override def mousePressed(e: MouseEvent): Unit = ()
          override def mouseReleased(e: MouseEvent): Unit = ()
          override def mouseEntered(e: MouseEvent): Unit = ()
          override def mouseExited(e: MouseEvent): Unit = ()
        })
      })
    })
    bar.setValue(12)

    hybridTick.setEnabled(true)
  }
}
