package gt4enginegrapher

import javax.swing.SwingUtilities

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import gt4enginegrapher.schema.AllSchema
import gt4enginegrapher.ui.{EngineBuilderFrame, EngineGraphPanel}
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

object Main extends AllSchema {
  def main(args: Array[String]): Unit = {
    implicit val schema: AllSchema = this
    implicit val db: JdbcDatabaseDef = usDb

    val allNames = Await.result(usDb.run(names.result).map(_.map(_.toSimpleName)), Duration.Inf)

    SwingUtilities.invokeLater { () =>
      val frame = new EngineBuilderFrame(allNames)
      frame.pack()
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)
    }
  }
}
