package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class DisplacementUp(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val price: Int,
  override val category: Int,
) extends HasTorqueRemapping
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Stock"
    case 1 => "Increased"
    case _ => "Invalid"
  }) + getSuffix
}

trait GT4DisplacementUpProvider {
  class DisplacementUpT(tag: Tag) extends GT4UpgradeTable[DisplacementUp](tag, "DISPLACEMENT") {
    override def * : ProvenShape[DisplacementUp] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((DisplacementUp.apply _).tupled, DisplacementUp.unapply)
  }

  lazy val displacementUps = TableQuery[DisplacementUpT]
}

trait GT3DisplacementUpProvider {
  class DisplacementUpT(tag: Tag) extends GT3UpgradeTable[DisplacementUp](tag, "DISPLACEMENT") {
    override def * : ProvenShape[DisplacementUp] =
      (
        LiteralColumn(0),
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((DisplacementUp.apply _).tupled, DisplacementUp.unapply)
  }

  lazy val displacementUps = TableQuery[DisplacementUpT]
}
