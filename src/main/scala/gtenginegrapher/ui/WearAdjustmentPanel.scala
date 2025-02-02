package gtenginegrapher.ui

import java.awt._
import java.awt.event.{ActionEvent, ActionListener, WindowEvent}

import javax.swing._

import gtenginegrapher.schema.{AllSchema, GT3AllSchema, GT4AllSchema, SimpleName}
import gtenginegrapher.wrappers.{WearAffectedByPrizeStatus, WearUnaffectedByPrizeStatus, WearValues}

class WearAdjustmentPanel(
  owner: JFrame,
  name: SimpleName,
  saveInfoF: ((BigDecimal, WearAdjustmentPanel.WearData)) => Unit,
  existingData: Option[WearAdjustmentPanel.WearData] = None,
)(implicit val schema: AllSchema, wear: WearValues)
  extends JDialog(owner, s"Wear Adjustment for ${name.name}")
  with ActionListener { adj =>
  private val submitCommand = "SUBMIT"

  private val (ticks, oilTick, carTick) = {
    val ((ots, _), (cts, _)) = existingData.getOrElse(WearAdjustmentPanel.defaultData)

    val oil = new JCheckBox("Have Changed Oil?") {
      setSelected(ots)
    }
    val car = new JCheckBox("Is Prize Car?") {
      setSelected(cts)
    }

    wear match {
      case _: WearUnaffectedByPrizeStatus => car.setEnabled(false)
      case _                              => car.setEnabled(true)
    }

    val panel = new JPanel {
      tkp =>
      private val usedLayout = new GridLayout(4, 1, 0, 4)
      tkp.setLayout(usedLayout)

      tkp.add(new JPanel(), 0)
      tkp.add(oil, 1)
      tkp.add(car, 2)
      tkp.add(new JPanel(), 3)
    }

    (panel, oil, car)
  }

  private val (inputsAndSubmit, oilInput, carInput) = {
    val ((_, oms), (_, cms)) = existingData.getOrElse(WearAdjustmentPanel.defaultData)

    val oil = UIUtils.positiveNumberOnlyTextField(_.setText(oms.toString))
    val car = UIUtils.positiveNumberOnlyTextField(_.setText(cms.toString))

    val panel = new JPanel {
      ikp =>
      private val usedLayout = new GridLayout(4, 1, 0, 4)
      ikp.setLayout(usedLayout)

      private val submitButton = new JButton("Submit")
      submitButton.addActionListener(adj)
      submitButton.setActionCommand(submitCommand)

      ikp.add(new JLabel("km Travelled Since") {
        setFont(getFont.deriveFont(Font.BOLD))
        setHorizontalAlignment(SwingConstants.CENTER)
      })
      ikp.add(oil, 1)
      ikp.add(car, 2)
      ikp.add(submitButton, 3)
    }

    (panel, oil, car)
  }

  setContentPane {
    new JPanel() { pan =>
      pan.setLayout {
        // I wish there was a cleaner constructor for this layout.
        new GridLayout(1, 2, 8, 0)
      }

      pan.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8))

      pan.add(ticks, 0)
      pan.add(inputsAndSubmit, 1)
    }
  }

  override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
    case s if s == submitCommand => submitInfo()
    case _                       => super.processEvent(e)
  }

  private def submitInfo(): Unit = {
    val oilMult = wear.oilMultiplier(oilInput.getText.toInt, oilTick.isSelected)
    val engineMult = wear match {
      case status: WearAffectedByPrizeStatus   =>
        status.engineMultiplier(carInput.getText.toInt, carTick.isSelected)
      case status: WearUnaffectedByPrizeStatus =>
        status.engineMultiplier(carInput.getText.toInt)
    }

    val saveData = (
      oilTick.isSelected -> oilInput.getText.toInt,
      carTick.isSelected -> carInput.getText.toInt,
    )

    saveInfoF(oilMult * engineMult, saveData)
    adj.dispatchEvent(new WindowEvent(adj, WindowEvent.WINDOW_CLOSING))
  }
}

object WearAdjustmentPanel {
  type WearData = ((Boolean, Int), (Boolean, Int))

  def defaultData(implicit schema: AllSchema): WearData = schema match {
    case _: GT3AllSchema => (false -> 0, true -> 0)
    case _: GT4AllSchema => (false -> 0, false -> 0)
  }
}
