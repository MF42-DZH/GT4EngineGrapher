package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._

abstract class SpecTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def rowId: Rep[Int] = column[Int]("RowId", O.PrimaryKey)
  def label: Rep[String] = column[String]("Label")
}

abstract class UpgradeTable[T](tag: Tag, name: String) extends SpecTable[T](tag, name) {
  def price: Rep[Int] = column[Int]("Price")
  def highRPMTorqueModifier: Rep[Int] = column[Int]("torquemodifier")
  def lowRPMTorqueModifier: Rep[Int] = column[Int]("torquemodifier2")
  def category: Rep[Int] = column("category")
}

abstract class UpgradeTableWithRevModifiers[T](
  tag: Tag,
  name: String,
  shiftLimitName: String,
  revLimitName: String,
) extends UpgradeTable[T](tag, name) {
  def shiftLimit: Rep[Int] = column[Int](shiftLimitName)
  def revLimit: Rep[Int] = column[Int](revLimitName)
}
