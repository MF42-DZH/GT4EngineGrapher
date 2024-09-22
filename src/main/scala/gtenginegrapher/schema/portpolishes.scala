package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class PortPolish(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val price: Int,
  override val category: Int,
) extends HasTorqueRemapping
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Not Applied"
    case 1 => "Applied"
    case _ => "Invalid"
  }) + getSuffix
}

trait GT4PortPolishProvider {
  class PortPolishT(tag: Tag) extends GT4UpgradeTable[PortPolish](tag, "PORTPOLISH") {
    override def * : ProvenShape[PortPolish] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((PortPolish.apply _).tupled, PortPolish.unapply)
  }

  lazy val portPolishes = TableQuery[PortPolishT]
}

trait GT3PortPolishProvider {
  class PortPolishT(tag: Tag) extends GT3UpgradeTable[PortPolish](tag, "PORTPOLISH") {
    override def * : ProvenShape[PortPolish] =
      (
        LiteralColumn(0),
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((PortPolish.apply _).tupled, PortPolish.unapply)
  }

  lazy val portPolishes = TableQuery[PortPolishT]
}
