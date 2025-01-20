package gtenginegrapher.ui

import java.awt._
import java.awt.event.{MouseEvent, MouseListener, WindowEvent}

import javax.swing._

import gtenginegrapher.utils._

class UnitPanel(
  owner: JFrame,
  saveInfoF: ((TorqueUnits.KeyVal, PowerUnits.KeyVal)) => Unit,
  existingData: (TorqueUnits.KeyVal, PowerUnits.KeyVal),
) extends JDialog(owner, s"Set Used Units") { up =>
  private val labels = new JPanel { ls =>
    ls.setLayout(new GridLayout(3, 1, 0, 4))

    ls.add(new JLabel("Torque Unit"), 0)
    ls.add(new JLabel("Power Unit"), 1)
    ls.add(new JPanel(), 2)
  }

  private val inputs = {
    val torque = new ConfigDropdown[TorqueUnits.type](TorqueUnits, existingData._1)
    val power = new ConfigDropdown[PowerUnits.type](PowerUnits, existingData._2)

    new JPanel { is =>
      is.setLayout(new GridLayout(3, 1, 0, 4))

      is.add(torque, 0)
      is.add(power, 1)
      is.add(
        new JButton("Submit") {
          addMouseListener(new MouseListener {
            override def mouseClicked(e: MouseEvent): Unit = {
              saveInfoF(
                (
                  torque.getSelectedItem.asInstanceOf[TorqueUnits.KeyVal],
                  power.getSelectedItem.asInstanceOf[PowerUnits.KeyVal],
                ),
              )

              up.dispatchEvent(new WindowEvent(up, WindowEvent.WINDOW_CLOSING))
            }

            override def mousePressed(e: MouseEvent): Unit = ()
            override def mouseReleased(e: MouseEvent): Unit = ()
            override def mouseEntered(e: MouseEvent): Unit = ()
            override def mouseExited(e: MouseEvent): Unit = ()
          })
        },
        2,
      )
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
