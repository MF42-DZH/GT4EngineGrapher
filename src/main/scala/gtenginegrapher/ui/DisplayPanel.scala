package gtenginegrapher.ui

import java.awt._
import java.awt.event.{ActionEvent, ActionListener, WindowEvent}

import javax.swing._

import gtenginegrapher.ui.UIUtils.RichJComboBox
import gtenginegrapher.utils._

class DisplayPanel(
  owner: JFrame,
  saveInfoF: ((TorqueUnits.KeyVal, PowerUnits.KeyVal, Boolean)) => Unit,
  existingData: (TorqueUnits.KeyVal, PowerUnits.KeyVal, Boolean),
) extends JDialog(owner, s"Set Display Options")
  with ActionListener { up =>
  private val torque = new ConfigDropdown[TorqueUnits.type](TorqueUnits, existingData._1)
  private val power = new ConfigDropdown[PowerUnits.type](PowerUnits, existingData._2)

  private val norm = new JCheckBox()
  norm.setSelected(existingData._3)
  norm.setHorizontalAlignment(SwingConstants.CENTER)

  private val submitCommand = "SUBMIT"

  override def actionPerformed(e: ActionEvent): Unit = e.getActionCommand match {
    case s if s == submitCommand =>
      saveInfoF((torque.getItem, power.getItem, norm.isSelected))
      up.dispatchEvent(new WindowEvent(up, WindowEvent.WINDOW_CLOSING))
    case _                       => super.processEvent(e)
  }

  private val (labelTorque, labelPower, labelNormalize) = (
    new JLabel("Torque Unit"),
    new JLabel("Power Unit"),
    new JLabel("Normalize Graphs"),
  )

  private val submitButton = new JButton("Submit")
  submitButton.addActionListener(up)
  submitButton.setActionCommand(submitCommand)

  setContentPane {
    new JPanel() { pan =>
      private val addComponent = UIUtils.initialiseGridBag(pan)
        .setInsets(new Insets(0, 0, 4, 8))
        .setFill(GridBagConstraints.BOTH)
        .setAnchor(GridBagConstraints.CENTER)

      pan.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 0))

      addComponent(labelTorque, 0, 0)
      addComponent(labelPower, 0, 1)
      addComponent(labelNormalize, 0, 2)
      addComponent(torque, 1, 0)
      addComponent(power, 1, 1)
      addComponent(norm, 1, 2)
      addComponent(submitButton, 0, 3, 2, 1)
    }
  }
}
