package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class DisplacementUp(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  category: Int,
) extends HasTorqueRemapping {
  override def toString: String = category match {
    case 0 => "Stock"
    case 1 => "Increased"
    case _ => "Invalid"
  }
}

trait DisplacementUpProvider {
  class DisplacementUpT(tag: Tag) extends UpgradeTable[DisplacementUp](tag, "DISPLACEMENT") {
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