package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class PortPolish(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  category: Int,
) extends HasTorqueRemapping

trait PortPolishProvider {
  class PortPolishT(tag: Tag) extends UpgradeTable[PortPolish](tag, "PORTPOLISH") {
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
