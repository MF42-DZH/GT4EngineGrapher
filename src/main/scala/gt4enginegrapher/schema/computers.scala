package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Computer(
  rowId: Int,
  label: String,
  override val highRPMTorqueModifier: Int,
  override val lowRPMTorqueModifier: Int,
  price: Int,
  override val category: Int,
) extends HasTorqueRemapping
  with CanHaveCarName {
  override def toString: String = (category match {
    case 0 => "Stock ECU"
    case 1 => "Sports ECU"
    case _ => "Invalid"
  }) + getSuffix
}

trait ComputerProvider {
  class ComputerT(tag: Tag) extends UpgradeTable[Computer](tag, "COMPUTER") {
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
