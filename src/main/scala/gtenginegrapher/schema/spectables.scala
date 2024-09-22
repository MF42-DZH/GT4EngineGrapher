package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._

sealed trait UpgradeTable[T] { self: Table[T] =>
  def highColumnName: String
  def lowColumnName: String

  def highRPMTorqueModifier: Rep[Int] = column[Int](highColumnName)
  def lowRPMTorqueModifier: Rep[Int] = column[Int](lowColumnName)

  def price: Rep[Int] = column[Int]("Price")
  def category: Rep[Int] = column("category")
}

sealed trait WithRevModifiers[T] { self: Table[T] with UpgradeTable[T] =>
  def shiftLimitName: String
  def revLimitName: String

  def shiftLimit: Rep[Int] = column[Int](shiftLimitName)
  def revLimit: Rep[Int] = column[Int](revLimitName)
}

abstract class SpecTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def label: Rep[String] = column[String]("Label")
}

abstract class GT3SpecTable[T](tag: Tag, name: String) extends SpecTable[T](tag, name) {
  def carLabel: Rep[String] = column[String]("CarLabel", O.PrimaryKey)
}

abstract class GT4SpecTable[T](tag: Tag, name: String) extends SpecTable[T](tag, name) {
  def rowId: Rep[Int] = column[Int]("RowId", O.PrimaryKey)
}

abstract class GT3UpgradeTable[T](tag: Tag, name: String)
  extends GT3SpecTable[T](tag, name)
  with UpgradeTable[T] {
  override val highColumnName: String = "torquemodifier2"
  override val lowColumnName: String = "torquemodifier"
}

abstract class GT4UpgradeTable[T](tag: Tag, name: String)
  extends GT4SpecTable[T](tag, name)
  with UpgradeTable[T] {
  override val highColumnName: String = "torquemodifier"
  override val lowColumnName: String = "torquemodifier2"
}

abstract class GT3UpgradeTableWithRevModifiers[T](
  tag: Tag,
  name: String,
  override val shiftLimitName: String,
  override val revLimitName: String,
) extends GT3UpgradeTable[T](tag, name)
  with WithRevModifiers[T]

abstract class GT4UpgradeTableWithRevModifiers[T](
  tag: Tag,
  name: String,
  override val shiftLimitName: String,
  override val revLimitName: String,
) extends GT4UpgradeTable[T](tag, name)
  with WithRevModifiers[T]
