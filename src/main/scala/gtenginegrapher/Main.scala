package gtenginegrapher

import javax.imageio.ImageIO
import javax.swing.{SwingUtilities, UIManager}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import gtenginegrapher.schema._
import gtenginegrapher.ui.EngineBuilderFrame
import gtenginegrapher.utils._
import gtenginegrapher.wrappers.{GT3Wear, GT4Wear, WearValues}
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

object Main extends SlickEscapes {
  private def printHelp(): Unit = println(
    """Gran Turismo Engine Grapher
      |
      |Usage: java -jar GTEngineGrapher.jar [GT3 | REGION | HELP] [VERSION] [VERBOSE]
      |
      |HELP        -h | --help
      |VERBOSE     -v | --verbose
      |REGION      NTSC-U | US    | USA | America       (Gran Turismo 4 NTSC-U)
      |            NTSC-K | KR    | KOR | Korea         (Gran Turismo 4 NTSC-K)
      |            NTSC-J | JP    | JAP | Japan         (Gran Turismo 4 NTSC-J)
      |            PAL    | EU    | EUR | Europe        (Gran Turismo 4 PAL)
      |            SPECII | SPEC2                       (Gran Turismo 4 Spec II; based on NTSC-U)
      |VERSION     1.05 | 1.06 | 1.07 | 1.08 | 1.09     (Spec II version; ignored if REGION is not Spec II; default: 1.09)
      |GT3         gt3 | GT3                            (Gran Turismo 3 NTSC-U)
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

    // TODO: Get a proper argument parser. This is getting out of hand.
    implicit val (schema: AllSchema, db: JdbcDatabaseDef, wear: WearValues, region: Region) =
      if (!args.exists(_.toLowerCase == "gt3")) {
        val gt4Schema: GT4AllSchema = new GT4AllSchema
        val (udb, reg): (JdbcDatabaseDef, Region) =
          args
            .collectFirst {
              case "ntsc-u" | "us" | "usa" | "america" => gt4Schema.usDb  -> NtscU
              case "ntsc-k" | "kr" | "kor" | "korea"   => gt4Schema.korDb -> NtscK
              case "ntsc-j" | "jp" | "jap" | "japan"   => gt4Schema.jpDb  -> NtscJ
              case "pal" | "eu" | "eur" | "europe"     => gt4Schema.euDb  -> Pal
              case "specii" | "spec2"                  =>
                args
                  .collectFirst {
                    case "1.05" => gt4Schema.s2Db_1_05 -> Spec2("v1.05")
                    case "1.06" => gt4Schema.s2Db_1_06 -> Spec2("v1.06.x")
                    case "1.07" => gt4Schema.s2Db_1_07 -> Spec2("v1.07")
                    case "1.08" => gt4Schema.s2Db_1_08 -> Spec2("v1.08")
                    case "1.09" => gt4Schema.s2Db_1_09 -> Spec2("v1.09")
                  }
                  .getOrElse(gt4Schema.s2Db_1_09 -> Spec2("v1.09"))
            }
            .getOrElse(gt4Schema.usDb -> NtscU)

        (gt4Schema.asBase, udb, GT4Wear.asBase, reg)
      } else {
        val gt3Schema: GT3AllSchema = new GT3AllSchema
        val udb = gt3Schema.usDb

        (gt3Schema.asBase, udb, GT3Wear.asBase, NtscU)
      }

    import schema._

    implicit val verbose: Boolean = args.map(_.toLowerCase).exists {
      case "-v"        => true
      case "--verbose" => true
      case _           => false
    }

    val allNames = db
      .run(
        names.result
          .withStatements(Main.getClass)
          .withCounting(Main.getClass),
      )
      .map(_.map(_.toSimpleName))
      .runBlocking

    UIManager.setLookAndFeel {
      val os = Option(System.getProperty("os.name"))

      os match {
        case Some(win) if win.toLowerCase.contains("win") => UIManager.getSystemLookAndFeelClassName
        case Some(mac) if mac.toLowerCase.contains("mac") => UIManager.getSystemLookAndFeelClassName
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
