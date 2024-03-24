package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class NATune(
  rowId: Int,
  label: String,
  price: Int,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  category: Int,
  override val shiftLimit: Int,
  override val revLimit: Int,
) extends HasTorqueRemapping
  with HasRevIncrease

trait NATunesProvider {
  class NATuneT(tag: Tag)
    extends UpgradeTableWithRevModifiers[NATune](
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
