package gtenginegrapher.schema

import java.io.File
import java.nio.file.Files

import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.backend.JdbcDatabaseDef

sealed trait AllSchema {
  type NameTable <: SpecTable[Name]
  def names: TableQuery[NameTable]

  type EngineTable <: SpecTable[Engine]
  def engines: TableQuery[EngineTable]

  type NATuneTable <: SpecTable[NATune]
  def naTunes: TableQuery[NATuneTable]

  type SuperchargerTable <: SpecTable[Supercharger]
  def superchargers: TableQuery[SuperchargerTable]

  type TurbineKitTable <: SpecTable[TurbineKit]
  def turbineKits: TableQuery[TurbineKitTable]

  type PortPolishTable <: SpecTable[PortPolish]
  def portPolishes: TableQuery[PortPolishTable]

  type EngineBalanceTable <: SpecTable[EngineBalance]
  def engineBalances: TableQuery[EngineBalanceTable]

  type MufflerTable <: SpecTable[Muffler]
  def mufflers: TableQuery[MufflerTable]

  type DisplacementUpTable <: SpecTable[DisplacementUp]
  def displacementUps: TableQuery[DisplacementUpTable]

  type ComputerTable <: SpecTable[Computer]
  def computers: TableQuery[ComputerTable]

  type IntercoolerTable <: SpecTable[Intercooler]
  def intercoolers: TableQuery[IntercoolerTable]

  type NitrousTable <: SpecTable[Nitrous]
  def nitrouses: TableQuery[NitrousTable]

  protected def createTempDb(resourcePath: String): JdbcDatabaseDef = {
    val tfile = File.createTempFile("specdb", ".sqlite")
    tfile.deleteOnExit()

    val stream = classOf[AllSchema].getResourceAsStream(resourcePath)
    Files.write(tfile.toPath, stream.readAllBytes())

    Database.forURL(s"jdbc:sqlite://${tfile.toPath}", driver = "org.sqlite.JDBC")
  }

  final val asBase: AllSchema = this
}

class GT3AllSchema
  extends GT3NameProvider
  with GT3EngineProvider
  with GT3NATunesProvider
  with GT3SuperchargerProvider
  with GT3TurbineKitProvider
  with GT3PortPolishProvider
  with GT3EngineBalanceProvider
  with GT3MufflerProvider
  with GT3DisplacementUpProvider
  with GT3ComputerProvider
  with GT3IntercoolerProvider
  with GT3NitrousProvider
  with AllSchema {
  lazy val usDb = createTempDb("/paramdb_us.sqlite")

  override type NameTable = NameT
  override type EngineTable = EngineT
  override type NATuneTable = NATuneT
  override type SuperchargerTable = SuperchargerT
  override type TurbineKitTable = TurbineKitT
  override type PortPolishTable = PortPolishT
  override type EngineBalanceTable = EngineBalanceT
  override type MufflerTable = MufflerT
  override type DisplacementUpTable = DisplacementUpT
  override type ComputerTable = ComputerT
  override type IntercoolerTable = IntercoolerT
  override type NitrousTable = NitrousT
}

class GT4AllSchema
  extends GT4NameProvider
  with GT4EngineProvider
  with GT4NATunesProvider
  with GT4SuperchargerProvider
  with GT4TurbineKitProvider
  with GT4PortPolishProvider
  with GT4EngineBalanceProvider
  with GT4MufflerProvider
  with GT4DisplacementUpProvider
  with GT4ComputerProvider
  with GT4IntercoolerProvider
  with GT4NitrousProvider
  with AllSchema {
  lazy val usDb = createTempDb("/GT4_PREMIUM_US2560.sqlite")
  lazy val jpDb = createTempDb("/GT4_PREMIUM_JP2560.sqlite")
  lazy val korDb = createTempDb("/GT4_KR2560.sqlite")
  lazy val euDb = createTempDb("/GT4_EU2560.sqlite")

  lazy val s2Db_1_05 = createTempDb("/GT4_PREMIUM_US2560_SPECII-v1_05.sqlite")
  lazy val s2Db_1_06 = createTempDb("/GT4_PREMIUM_US2560_SPECII-v1_06.sqlite")

  override type NameTable = NameT
  override type EngineTable = EngineT
  override type NATuneTable = NATuneT
  override type SuperchargerTable = SuperchargerT
  override type TurbineKitTable = TurbineKitT
  override type PortPolishTable = PortPolishT
  override type EngineBalanceTable = EngineBalanceT
  override type MufflerTable = MufflerT
  override type DisplacementUpTable = DisplacementUpT
  override type ComputerTable = ComputerT
  override type IntercoolerTable = IntercoolerT
  override type NitrousTable = NitrousT
}
