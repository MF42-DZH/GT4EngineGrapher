package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Intercooler(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  category: Int,
) extends HasTorqueRemapping

trait IntercoolerProvider {
  class IntercoolerT(tag: Tag) extends UpgradeTable[Intercooler](tag, "INTERCOOLER") {
    override def * : ProvenShape[Intercooler] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Intercooler.apply _).tupled, Intercooler.unapply)
  }

  lazy val intercoolers = TableQuery[IntercoolerT]
}
