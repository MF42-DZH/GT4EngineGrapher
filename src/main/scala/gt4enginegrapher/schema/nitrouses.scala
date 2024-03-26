package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Nitrous(
  rowId: Int,
  label: String,
  _unused: Int,
  capacity: Int,
  override val category: Int,
  defaultSetting: Int,
  minSetting: Int,
  maxSetting: Int,
) extends CanHaveCarName {
  def toUsedNitrous(setting: Int): Option[UsedNitrous] = setting match {
    case s if s >= minSetting && s <= maxSetting => Some(UsedNitrous(this, setting, category))
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
  override val category: Int,
) extends HasTorqueRemapping {
  override def highRPMTorqueModifier: Int = 100 + setting
  override def lowRPMTorqueModifier: Int = 100 + setting
}

trait NitrousProvider {
  class NitrousT(tag: Tag) extends SpecTable[Nitrous](tag, "NOS") {
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
        category,
        defaultSetting,
        minSetting,
        maxSetting,
      ) <> ((Nitrous.apply _).tupled, Nitrous.unapply)
  }

  lazy val nitrouses = TableQuery[NitrousT]
}
