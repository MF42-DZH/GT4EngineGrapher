package gt4enginegrapher.schema

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Name(
  rowId: Int,
  label: String,
  name: String,
  grade: String,
  shortName: String,
  narrationId: Int,
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
)

trait NameProvider {
  class NameT(tag: Tag) extends SpecTable[Name](tag, "CAR_NAME_american") {
    def name = column[String]("Name")
    def grade = column[String]("Grade")
    def shortName = column[String]("ShortName")
    def narrationId = column[Int]("NarrationID")

    override def * : ProvenShape[Name] =
      (
        rowId,
        label,
        name,
        grade,
        shortName,
        narrationId,
      ) <> ((Name.apply _).tupled, Name.unapply)
  }

  lazy val names = TableQuery[NameT]
}
