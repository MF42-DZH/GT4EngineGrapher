package gt4enginegrapher

import javax.swing.SwingUtilities

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import gt4enginegrapher.schema.AllSchema
import gt4enginegrapher.ui.EngineGraphFrame
import slick.jdbc.SQLiteProfile.api._

object Main extends AllSchema {
  def main(args: Array[String]): Unit = {
    val allEngines = Await.result(usDb.run(engines.result), Duration.Inf)
    val allNames = Await.result(usDb.run(names.result), Duration.Inf)
    val namedEngines = allNames
      .map(name => (name, allEngines.find(engine => engine.label.contains(name.label))))
      .collect { case (n, Some(e)) => (n, e) }

    SwingUtilities.invokeLater { () =>
      val Some(theCar) = namedEngines.find { case (n, _) =>
        n.name.contains("SLR")
      }
      val chart = EngineGraphFrame(theCar._1, theCar._2.toSimpleEngine)
      println(theCar._2.toSimpleEngine)

      chart.pack()
      chart.setLocationRelativeTo(null)
      chart.setVisible(true)
    }
  }
}
