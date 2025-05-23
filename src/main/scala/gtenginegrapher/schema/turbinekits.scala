package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class TurbineKit(
  rowId: Int,
  label: String,
  override val price: Int,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val category: Int,
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
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Not Applied / Stock"
    case _ => s"Stage $category"
  }) + getSuffix
}

trait GT4TurbineKitProvider {
  class TurbineKitT(tag: Tag)
    extends GT4UpgradeTableWithRevModifiers[TurbineKit](
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

trait GT3TurbineKitProvider {
  class TurbineKitT(tag: Tag)
    extends GT3UpgradeTableWithRevModifiers[TurbineKit](
      tag            = tag,
      name           = "TURBINEKIT",
      shiftLimitName = "rpmPointsModifier",
      revLimitName   = "revlimitModifier",
    ) {
    override val highColumnName: String = "torqueMultiplier2"
    override val lowColumnName: String = "torqueMultiplier"

    def wastegate = column[Int]("wastegate")
    def boost1 = column[Int]("boost1")
    def peakRpm1 = column[Int]("peakrpm1")
    def response1 = column[Int]("response1")
    def boost2 = column[Int]("boost2")
    def peakRpm2 = column[Int]("peakrpm2")
    def response2 = column[Int]("response2")

    override def * : ProvenShape[TurbineKit] =
      (
        LiteralColumn(0),
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
