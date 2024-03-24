package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Muffler(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  category: Int,
) extends HasTorqueRemapping {
  override def toString: String = category match {
    case 0 => "Not Applied / Stock"
    case 1 => "Sports"
    case 2 => "Semi-Racing"
    case 3 => "Racing"
    case _ => "Generic"
  }
}

trait MufflerProvider {
  class MufflerT(tag: Tag) extends UpgradeTable[Muffler](tag, "MUFFLER") {
    override def * : ProvenShape[Muffler] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Muffler.apply _).tupled, Muffler.unapply)
  }

  lazy val mufflers = TableQuery[MufflerT]
}
