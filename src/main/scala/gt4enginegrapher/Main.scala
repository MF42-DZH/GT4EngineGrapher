package gt4enginegrapher

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import gt4enginegrapher.schema.AllSchema
import slick.jdbc.SQLiteProfile.api._

object Main extends AllSchema {
  def main(args: Array[String]): Unit = {
    val allEngines = Await.result(usDb.run(engines.result), Duration.Inf)
    val allNames = Await.result(usDb.run(names.result), Duration.Inf)
    val namedEngines = allNames
      .map(name => (name, allEngines.find(engine => engine.label.contains(name.label))))
      .collect { case (n, Some(e)) => (n, e) }

    println(namedEngines.head)
  }
}
