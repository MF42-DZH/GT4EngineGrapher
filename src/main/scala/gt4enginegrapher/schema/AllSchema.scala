package gt4enginegrapher.schema

import java.io.File
import java.nio.file.{Files, Paths}

import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

trait AllSchema
  extends NameProvider
  with EngineProvider
  with NATunesProvider
  with SuperchargerProvider
  with TurbineKitProvider
  with PortPolishProvider
  with EngineBalanceProvider
  with MufflerProvider
  with DisplacementUpProvider
  with ComputerProvider
  with IntercoolerProvider
  with NitrousProvider {
  private def createTempDb(resourcePath: String): JdbcDatabaseDef = {
    val tfile = File.createTempFile("specdb", ".sqlite")
    tfile.deleteOnExit()

    val stream = classOf[AllSchema].getResourceAsStream(resourcePath)
    Files.write(tfile.toPath, stream.readAllBytes())

    Database.forURL(s"jdbc:sqlite://${tfile.toPath}", driver = "org.sqlite.JDBC")
  }

  lazy val usDb = createTempDb("/GT4_PREMIUM_US2560.sqlite")
  lazy val jpDb = createTempDb("/GT4_PREMIUM_JP2560.sqlite")
  lazy val korDb = createTempDb("/GT4_KR2560.sqlite")
  lazy val euDb = createTempDb("/GT4_EU2560.sqlite")
}
