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

  private val labels = new JPanel { ls =>
    ls.setLayout(new GridLayout(4, 1, 0, 4))

    ls.add(new JLabel("Torque Unit"), 0)
    ls.add(new JLabel("Power Unit"), 1)
    ls.add(new JLabel("Normalize Graphs"), 2)
    ls.add(new JPanel(), 3)
  }

  private val inputs = {
    new JPanel { is =>
      is.setLayout(new GridLayout(4, 1, 0, 4))

      private val submitButton = new JButton("Submit")
      submitButton.addActionListener(up)
      submitButton.setActionCommand(submitCommand)

      is.add(torque, 0)
      is.add(power, 1)
      is.add(norm, 2)
      is.add(submitButton, 3)
    }
  }

  setContentPane {
    new JPanel() { pan =>
      pan.setLayout {
        new GridLayout(1, 2, 8, 0)
      }

      pan.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8))

      pan.add(labels, 0)
      pan.add(inputs, 1)
    }
  }
}
