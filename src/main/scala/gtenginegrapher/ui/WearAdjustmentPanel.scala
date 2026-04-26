package gtenginegrapher.ui

import java.awt._
import java.awt.event.{ActionEvent, ActionListener, WindowEvent}

import javax.swing._

import gtenginegrapher.schema.{
  AllSchema,
  GT3AllSchema,
  GT4AllSchema,
  GTCAllSchema,
  GTPspAllSchema,
  SimpleName,
}
import gtenginegrapher.wrappers.{
  WearAffectedByPrizeStatus,
  WearNonexistent,
  WearUnaffectedByPrizeStatus,
  WearValues,
}

class WearAdjustmentPanel(
  owner: JFrame,
  name: SimpleName,
  saveInfoF: ((BigDecimal, WearAdjustmentPanel.WearData)) => Unit,
  existingData: Option[WearAdjustmentPanel.WearData] = None,
)(implicit val schema: AllSchema, wear: WearValues)
  extends JDialog(owner, s"Wear Adjustment for ${name.name}")
  with ActionListener { adj =>
  private val submitCommand = "SUBMIT"

  private val (oilTick, carTick) = {
    val ((ots, _), (cts, _)) = existingData.getOrElse(WearAdjustmentPanel.defaultData)

    val oil = new JCheckBox("Have Changed Oil?") {
      setSelected(ots)
    }
    val car = new JCheckBox("Is Prize Car?") {
      setSelected(cts)
    }

    wear match {
      case _: WearNonexistent             =>
        oil.setEnabled(false)
        car.setEnabled(false)
      case _: WearUnaffectedByPrizeStatus =>
        oil.setEnabled(true)
        car.setEnabled(false)
      case _                              =>
        oil.setEnabled(true)
        car.setEnabled(true)
    }

    (oil, car)
  }

  private val (travelLabel, oilInput, carInput, submitButton) = {
    val ((_, oms), (_, cms)) = existingData.getOrElse(WearAdjustmentPanel.defaultData)

    val oil = UIUtils.positiveNumberOnlyTextField(_.setText(oms.toString))
    val car = UIUtils.positiveNumberOnlyTextField(_.setText(cms.toString))

    if (wear.isInstanceOf[WearNonexistent]) {
      oil.setEnabled(false)
      car.setEnabled(false)
    }

    val submit = new JButton("Submit")
    submit.addActionListener(adj)
    submit.setActionCommand(submitCommand)

    val rightLabel = new JLabel("km Travelled Since") {
      setFont(getFont.deriveFont(Font.BOLD))
      setHorizontalAlignment(SwingConstants.CENTER)
    }

    (rightLabel, oil, car, submit)
  }

  setContentPane {
    new JPanel() { pan =>
      private val addComponent = UIUtils
        .initialiseGridBag(pan)
        .setInsets(new Insets(0, 0, 4, 8))
        .setFill(GridBagConstraints.BOTH)
        .setAnchor(GridBagConstraints.CENTER)

      pan.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 0))

      addComponent(oilTick, 0, 1)
      addComponent(carTick, 0, 2)
      addComponent(travelLabel, 1, 0)
      addComponent(oilInput, 1, 1)
      addComponent(carInput, 1, 2)
      addComponent(submitButton, 0, 3, 2, 1)
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
      case _                                   => BigDecimal(1)
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
    case _: GT3AllSchema   => (false -> 0, true -> 0)
    case _: GT4AllSchema   => (false -> 0, false -> 0)
    case _: GTPspAllSchema => (false -> 0, false -> 0)
    case _: GTCAllSchema   => (false -> 0, false -> 0)
  }
}
