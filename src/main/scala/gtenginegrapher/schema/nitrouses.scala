package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Nitrous(
  rowId: Int,
  label: String,
  _unused: Int,
  override val price: Int,
  capacity: Int,
  override val category: Int,
  defaultSetting: Int,
  minSetting: Int,
  maxSetting: Int,
) extends CanHaveCarName {
  def toUsedNitrous(setting: Int): Option[UsedNitrous] = setting match {
    case s if s >= minSetting && s <= maxSetting =>
      Some(UsedNitrous(this, setting, price, category))
    case _                                       => None
  }

  override def toString: String = (category match {
    case 0 => "Not Applied"
    case 1 => "Applied"
    case _ => "Invalid"
  }) + getSuffix
}

case class UsedNitrous(
  specs: Nitrous,
  setting: Int,
  override val price: Int,
  override val category: Int,
) extends HasTorqueRemapping {
  override def highRPMTorqueModifier: Int = 100 + setting
  override def lowRPMTorqueModifier: Int = 100 + setting
}

trait GT4NitrousProvider {
  class NitrousT(tag: Tag) extends GT4SpecTable[Nitrous](tag, "NOS") {
    def _unused = column[Int]("Unk")
    def capacity = column[Int]("Capacity")
    def price = column[Int]("Price")
    def category = column[Int]("category")
    def defaultSetting = column[Int]("TorqueVolume")
    def minSetting = column[Int]("TorqueVolumeMin")
    def maxSetting = column[Int]("TorqueVolumeMax")

    override def * : ProvenShape[Nitrous] =
      (
        rowId,
        label,
        _unused,
        price,
        capacity,
        category,
        defaultSetting,
        minSetting,
        maxSetting,
      ) <> ((Nitrous.apply _).tupled, Nitrous.unapply)
  }

  lazy val nitrouses = TableQuery[NitrousT]
}

// This table does not exist. It is only here for completeness' sake.
// "ASCC" used as stand-in so SELECT does not fail due to non-existent table.
trait GT3NitrousProvider {
  class NitrousT(tag: Tag) extends GT3SpecTable[Nitrous](tag, "ASCC") {
    override def * : ProvenShape[Nitrous] =
      (
        LiteralColumn(0),
        LiteralColumn("UNDEFINED"),
        LiteralColumn(0),
        LiteralColumn(0),
        LiteralColumn(0),
        LiteralColumn(0),
        LiteralColumn(0),
        LiteralColumn(0),
        LiteralColumn(0),
      ) <> ((Nitrous.apply _).tupled, Nitrous.unapply)
  }

  lazy val nitrouses = TableQuery[NitrousT]
}
