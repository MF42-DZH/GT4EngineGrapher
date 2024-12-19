package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class NATune(
  rowId: Int,
  label: String,
  override val price: Int,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val category: Int,
  override val shiftLimit: Int,
  override val revLimit: Int,
) extends HasTorqueRemapping
  with HasRevIncrease
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Not Applied / Stock"
    case _ => s"Stage $category"
  }) + getSuffix
}

trait GT4NATunesProvider {
  class NATuneT(tag: Tag)
    extends GT4UpgradeTableWithRevModifiers[NATune](
      tag            = tag,
      name           = "NATUNE",
      shiftLimitName = "shiftlimit",
      revLimitName   = "revlimit",
    ) {
    override def * : ProvenShape[NATune] =
      (
        rowId,
        label,
        price,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        category,
        shiftLimit,
        revLimit,
      ) <> ((NATune.apply _).tupled, NATune.unapply)
  }

  lazy val naTunes = TableQuery[NATuneT]
}

trait GT3NATunesProvider {
  class NATuneT(tag: Tag)
    extends GT3UpgradeTableWithRevModifiers[NATune](
      tag            = tag,
      name           = "NATUNE",
      shiftLimitName = "rpmPointsModifier",
      revLimitName   = "revlimitModifier",
    ) {
    override val highColumnName: String = "torqueModifier2"
    override val lowColumnName: String = "torqueModifier"

    override def * : ProvenShape[NATune] =
      (
        LiteralColumn(0),
        label,
        price,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        category,
        shiftLimit,
        revLimit,
      ) <> ((NATune.apply _).tupled, NATune.unapply)
  }

  lazy val naTunes = TableQuery[NATuneT]
}
