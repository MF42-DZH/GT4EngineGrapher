package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Supercharger(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  category: Int,
) extends HasTorqueRemapping {
  override def toString: String = category match {
    case 0 => "Not Applied"
    case 1 => "Applied"
    case _ => "Invalid"
  }
}

trait SuperchargerProvider {
  class SuperchargerT(tag: Tag) extends UpgradeTable[Supercharger](tag, "SUPERCHARGER") {
    override def * : ProvenShape[Supercharger] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Supercharger.apply _).tupled, Supercharger.unapply)
  }

  lazy val superchargers = TableQuery[SuperchargerT]
}
