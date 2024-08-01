package gt4enginegrapher.utils

import scala.language.implicitConversions

import slick.jdbc.SQLiteProfile.api._

trait SlickEscapes {
  implicit def tableElementEscape[E, T <: Table[E]](what: T#TableElementType): E =
    what.asInstanceOf[E]
}
