package gtenginegrapher.ui

import java.awt.{List => _, _}
import javax.swing._
import javax.swing.event.{DocumentEvent, DocumentListener}

import scala.util.Try

object UIUtils {
  def positiveNumberOnlyTextField(
    modifications: JFormattedTextField => Unit = _ => (),
  ): JFormattedTextField = {
    val field = new JFormattedTextField()
    modifications(field)

    field.getDocument.addDocumentListener {
      new DocumentListener {
        override def insertUpdate(e: DocumentEvent): Unit =
          SwingUtilities.invokeLater { () =>
            val text = field.getText

            // Chop out the offending part of the text box if not a number.
            if (!text.matches(raw"\d+")) {
              field.setText {
                text.substring(0, e.getOffset) +
                  Try { text.substring(e.getOffset + e.getLength) }.getOrElse("")
              }
            }
          }

        override def removeUpdate(e: DocumentEvent): Unit = ()
        override def changedUpdate(e: DocumentEvent): Unit = ()
      }
    }

    field
  }

  implicit class RichJComboBox[E](cb: JComboBox[E]) {
    def getItem: E = cb.getSelectedItem.asInstanceOf[E]
  }

  // Utilities to make working with a GridBagLayout container much easier.
  class ComponentAdder(parent: Container, layout: GridBagLayout, constraints: GridBagConstraints) {
    def apply(child: Component, x: Int, y: Int): ComponentAdder =
      apply(child, x, y, 1, 1)

    def apply(child: Component, x: Int, y: Int, w: Int, h: Int): ComponentAdder = {
      constraints.gridx = x
      constraints.gridy = y
      constraints.gridwidth = w
      constraints.gridheight = h

      layout.setConstraints(child, constraints)
      parent.add(child)

      this
    }

    def setWeights(newWeightX: Double, newWeightY: Double): ComponentAdder = {
      constraints.weightx = newWeightX
      constraints.weighty = newWeightY

      this
    }

    def setInsets(newInsets: Insets): ComponentAdder = { constraints.insets = newInsets; this }
    def setAnchor(newAnchor: Int): ComponentAdder = { constraints.anchor = newAnchor; this }
    def setFill(newFill: Int): ComponentAdder = { constraints.fill = newFill; this }

    def setIPadding(newIPadX: Int, newIPadY: Int): ComponentAdder = {
      constraints.ipadx = newIPadX
      constraints.ipady = newIPadY

      this
    }
  }

  def initialiseGridBag(parentContainer: Container): ComponentAdder = {
    val layout = new GridBagLayout
    val constraints = new GridBagConstraints()

    parentContainer.setLayout(layout)
    new ComponentAdder(parentContainer, layout, constraints)
  }
}
