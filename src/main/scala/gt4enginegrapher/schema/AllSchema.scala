package gt4enginegrapher.schema

import java.nio.file.Paths

import slick.jdbc.SQLiteProfile.api._

trait AllSchema extends EngineProvider with NameProvider {
  lazy val usDb = {
    val dbPath = Paths.get(classOf[AllSchema].getResource("/GT4_PREMIUM_US2560.sqlite").toURI)
    Database.forURL(s"jdbc:sqlite://$dbPath", driver = "org.sqlite.JDBC")
  }

  lazy val jpDb = {
    val dbPath = Paths.get(classOf[AllSchema].getResource("/GT4_PREMIUM_JP2560.sqlite").toURI)
    Database.forURL(s"jdbc:sqlite://$dbPath", driver = "org.sqlite.JDBC")
  }

  lazy val korDb = {
    val dbPath = Paths.get(classOf[AllSchema].getResource("/GT4_KR2560.sqlite").toURI)
    Database.forURL(s"jdbc:sqlite://$dbPath", driver = "org.sqlite.JDBC")
  }

  lazy val euDb = {
    val dbPath = Paths.get(classOf[AllSchema].getResource("/GT4_EU2560.sqlite").toURI)
    Database.forURL(s"jdbc:sqlite://$dbPath", driver = "org.sqlite.JDBC")
  }
}
