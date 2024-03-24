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
  private def printHelp(): Unit = println(
    """GT4 Engine Grapher
      |
      |Usage: java -jar GT4EngineGrapher.jar [REGION | HELP]
      |
      |HELP       -h | --help
      |REGION     NTSC-U | US | USA | America     (NTSC-U)
      |           NTSC-K | KR | KOR | Korea       (NTSC-K)
      |           NTSC-J | JP | JAP | Japan       (NTSC-J)
      |           PAL    | EU | EUR | Europe      (PAL)
      |
      |If REGION is not specified, NTSC-U is assumed.""".stripMargin,
  )

  def main(args: Array[String]): Unit = {
    args match {
      case _ if args.contains("--help") || args.contains("-h") =>
        printHelp()
        return
      case _                                                   => ()
    }

    implicit val schema: AllSchema = this
    implicit val db: JdbcDatabaseDef = args match {
      case Array(region) =>
        region.toLowerCase match {
          case "ntsc-u" | "us" | "usa" | "america" => usDb
          case "ntsc-k" | "kr" | "kor" | "korea"   => korDb
          case "ntsc-j" | "jp" | "jap" | "japan"   => jpDb
          case "pal" | "eu" | "eur" | "europe"     => euDb
        }
      case _             => usDb
    }

    val allNames = Await.result(db.run(names.result).map(_.map(_.toSimpleName)), Duration.Inf)

    SwingUtilities.invokeLater { () =>
      val frame = new EngineBuilderFrame(allNames)
      frame.pack()
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)
    }
  }
}
