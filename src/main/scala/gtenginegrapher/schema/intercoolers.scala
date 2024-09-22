package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Intercooler(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val price: Int,
  override val category: Int,
) extends HasTorqueRemapping
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Not Applied / Stock"
    case 1 => "Sports"
    case 2 => "Racing"
    case _ => "Generic"
  }) + getSuffix
}

trait GT4IntercoolerProvider {
  class IntercoolerT(tag: Tag) extends GT4UpgradeTable[Intercooler](tag, "INTERCOOLER") {
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

trait GT3IntercoolerProvider {
  class IntercoolerT(tag: Tag) extends GT3UpgradeTable[Intercooler](tag, "INTERCOOLER") {
    override def * : ProvenShape[Intercooler] =
      (
        LiteralColumn(0),
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Intercooler.apply _).tupled, Intercooler.unapply)
  }

  lazy val intercoolers = TableQuery[IntercoolerT]
}
