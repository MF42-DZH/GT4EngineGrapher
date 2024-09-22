package gtenginegrapher.ui

import javax.swing._
import javax.swing.event.{DocumentEvent, DocumentListener}

import scala.util.Try

object UIUtils {
  def numberOnlyTextField(
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
}
