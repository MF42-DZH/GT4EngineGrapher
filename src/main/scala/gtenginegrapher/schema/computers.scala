package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Computer(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  override val price: Int,
  override val category: Int,
) extends HasTorqueRemapping
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Stock ECU"
    case 1 => "Sports ECU"
    case _ => "Invalid"
  }) + getSuffix
}

trait GT4ComputerProvider {
  class ComputerT(tag: Tag) extends GT4UpgradeTable[Computer](tag, "COMPUTER") {
    override def * : ProvenShape[Computer] =
      (
        rowId,
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Computer.apply _).tupled, Computer.unapply)
  }

  lazy val computers = TableQuery[ComputerT]
}

trait GT3ComputerProvider {
  class ComputerT(tag: Tag) extends GT3UpgradeTable[Computer](tag, "COMPUTER") {
    override def * : ProvenShape[Computer] =
      (
        LiteralColumn(0),
        label,
        highRPMTorqueModifier,
        lowRPMTorqueModifier,
        price,
        category,
      ) <> ((Computer.apply _).tupled, Computer.unapply)
  }

  lazy val computers = TableQuery[ComputerT]
}
