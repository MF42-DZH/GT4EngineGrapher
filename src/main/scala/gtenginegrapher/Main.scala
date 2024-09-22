package gtenginegrapher

import javax.imageio.ImageIO
import javax.swing.{SwingUtilities, UIManager}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import gtenginegrapher.schema._
import gtenginegrapher.ui.EngineBuilderFrame
import gtenginegrapher.wrappers.{GT3Wear, GT4Wear, WearValues}
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

object Main {
  private def printHelp(): Unit = println(
    """Gran Turismo Engine Grapher
      |
      |Usage: java -jar GTEngineGrapher.jar [GT3 | REGION | HELP]
      |
      |HELP       -h | --help
      |REGION     NTSC-U | US | USA | America     (Gran Turismo 4 NTSC-U)
      |           NTSC-K | KR | KOR | Korea       (Gran Turismo 4 NTSC-K)
      |           NTSC-J | JP | JAP | Japan       (Gran Turismo 4 NTSC-J)
      |           PAL    | EU | EUR | Europe      (Gran Turismo 4 PAL)
      |GT3        gt3 | GT3                       (Gran Turismo 3 NTSC-U)
      |
      |If REGION is not specified, GT4 NTSC-U is assumed.""".stripMargin,
  )

  def main(args: Array[String]): Unit = {
    args match {
      case _ if args.contains("--help") || args.contains("-h") =>
        printHelp()
        return
      case _                                                   => ()
    }

    implicit val (schema: AllSchema, db: JdbcDatabaseDef, wear: WearValues) =
      if (!args.exists(_.toLowerCase == "gt3")) {
        val gt4Schema: GT4AllSchema = new GT4AllSchema
        val udb: JdbcDatabaseDef =
          args
            .collectFirst {
              case "ntsc-u" | "us" | "usa" | "america" => gt4Schema.usDb
              case "ntsc-k" | "kr" | "kor" | "korea"   => gt4Schema.korDb
              case "ntsc-j" | "jp" | "jap" | "japan"   => gt4Schema.jpDb
              case "pal" | "eu" | "eur" | "europe"     => gt4Schema.euDb
            }
            .getOrElse(gt4Schema.usDb)

        (gt4Schema.asBase, udb, GT4Wear.asBase)
      } else {
        val gt3Schema: GT3AllSchema = new GT3AllSchema
        val udb = gt3Schema.usDb

        (gt3Schema.asBase, udb, GT3Wear.asBase)
      }

    import schema._

    val allNames = Await.result(db.run(names.result).map(_.map(_.toSimpleName)), Duration.Inf)

    UIManager.setLookAndFeel {
      val os = System.getProperty("os.name")

      os match {
        case win if win.toLowerCase.contains("win") => UIManager.getSystemLookAndFeelClassName
        case mac if mac.toLowerCase.contains("mac") => UIManager.getSystemLookAndFeelClassName
        case _ => UIManager.getCrossPlatformLookAndFeelClassName
      }
    }

    SwingUtilities.invokeLater { () =>
      val frame = new EngineBuilderFrame(allNames)
      frame.setIconImage(ImageIO.read(Main.getClass.getResourceAsStream("/engine-analysis.png")))
      frame.pack()
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)
    }
  }
}
