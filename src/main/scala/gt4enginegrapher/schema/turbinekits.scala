package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class TurbineKit(
  rowId: Int,
  label: String,
  price: Int,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  category: Int,
  wastegate: Int,
  boost1: Int,
  peakRpm1: Int,
  response1: Int,
  boost2: Int,
  peakRpm2: Int,
  response2: Int,
  override val shiftLimit: Int,
  override val revLimit: Int,
) extends HasTorqueRemapping
  with HasRevIncrease

trait TurbineKitProvider {
  class TurbineKitT(tag: Tag)
    extends UpgradeTableWithRevModifiers[TurbineKit](
      tag            = tag,
      name           = "TURBINEKIT",
      shiftLimitName = "shiftlimit",
      revLimitName   = "revlimit",
    ) {
    def wastegate = column[Int]("wastegate")
    def boost1 = column[Int]("boost1")
    def peakRpm1 = column[Int]("peakrpm1")
    def response1 = column[Int]("response1")
    def boost2 = column[Int]("boost2")
    def peakRpm2 = column[Int]("peakrpm2")
    def response2 = column[Int]("response2")

    override def * : ProvenShape[TurbineKit] =
      (
        rowId,
        label,
        price,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        category,
        wastegate,
        boost1,
        peakRpm1,
        response1,
        boost2,
        peakRpm2,
        response2,
        shiftLimit,
        revLimit,
      ) <> ((TurbineKit.apply _).tupled, TurbineKit.unapply)
  }

  lazy val turbineKits = TableQuery[TurbineKitT]
}
