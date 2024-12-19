package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Supercharger(
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

trait GT4SuperchargerProvider {
  class SuperchargerT(tag: Tag) extends GT4UpgradeTable[Supercharger](tag, "SUPERCHARGER") {
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

// This table does not exist. It is only here for completeness' sake.
// "ASCC" used as stand-in so SELECT does not fail due to non-existent table.
trait GT3SuperchargerProvider {
  class SuperchargerT(tag: Tag) extends GT3UpgradeTable[Supercharger](tag, "ASCC") {
    override def * : ProvenShape[Supercharger] =
      (
        LiteralColumn(0),
        LiteralColumn("UNDEFINED"),
        LiteralColumn(100),
        LiteralColumn(100),
        LiteralColumn(0),
        LiteralColumn(0),
      ) <> ((Supercharger.apply _).tupled, Supercharger.unapply)
  }

  lazy val superchargers = TableQuery[SuperchargerT]
}
