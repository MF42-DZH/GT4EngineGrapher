package gtenginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Name(
  rowId: Int,
  label: String,
  name: String,
) {
  private def normaliseSpaces(str: String): String = str.replaceAll(raw"\s{2,}", " ")

  lazy val toSimpleName: SimpleName = SimpleName(
    label = label,
    name  = normaliseSpaces(name),
  )
}

case class SimpleName(
  label: String,
  name: String,
) {
  override def toString: String = s"$name${if (!label.contains("not_a_car")) s" ($label)" else ""}"
}

trait GT4NameProvider {
  class NameT(tag: Tag) extends GT4SpecTable[Name](tag, "CAR_NAME_american") {
    def name = column[String]("Name")

    override def * : ProvenShape[Name] =
      (
        rowId,
        label,
        name,
      ) <> ((Name.apply _).tupled, Name.unapply)
  }

  lazy val names = TableQuery[NameT]
}

trait GT3NameProvider {
  class NameT(tag: Tag) extends GT3SpecTable[Name](tag, "CAR") {
    override def label: Rep[String] = column[String]("Car")

    def name =
      column[String]("MakerName") ++
        " " ++
        column[String]("NameFirstPartUS") ++
        " " ++
        column[String]("NameSecondPartUS")

    override def * : ProvenShape[Name] =
      (
        label,
        name,
      ) <> ((Name(0, _, _)).tupled, (name: Name) => Some((name.label, name.name)))
  }

  lazy val names = TableQuery[NameT]
}
