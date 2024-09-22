package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class EngineBalance(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val price: Int,
  override val category: Int,
  override val shiftLimit: Int,
  override val revLimit: Int,
) extends HasTorqueRemapping
  with HasRevIncrease
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Not Applied"
    case 1 => "Applied"
    case _ => "Invalid"
  }) + getSuffix
}

trait GT4EngineBalanceProvider {
  class EngineBalanceT(tag: Tag)
    extends GT4UpgradeTableWithRevModifiers[EngineBalance](
      tag            = tag,
      name           = "ENGINEBALANCE",
      shiftLimitName = "Unk",
      revLimitName   = "Unk_1",
    ) {
    override def * : ProvenShape[EngineBalance] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
        shiftLimit,
        revLimit,
      ) <> ((EngineBalance.apply _).tupled, EngineBalance.unapply)
  }

  lazy val engineBalances = TableQuery[EngineBalanceT]
}

trait GT3EngineBalanceProvider {
  class EngineBalanceT(tag: Tag)
    extends GT3UpgradeTableWithRevModifiers[EngineBalance](
      tag            = tag,
      name           = "ENGINEBALANCE",
      shiftLimitName = "rpmPointsModifier",
      revLimitName   = "revlimitModifier",
    ) {
    override def * : ProvenShape[EngineBalance] =
      (
        LiteralColumn(0),
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
        shiftLimit,
        revLimit,
      ) <> ((EngineBalance.apply _).tupled, EngineBalance.unapply)
  }

  lazy val engineBalances = TableQuery[EngineBalanceT]
}
