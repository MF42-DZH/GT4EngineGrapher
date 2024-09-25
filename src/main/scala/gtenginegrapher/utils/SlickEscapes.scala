package gtenginegrapher.utils

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

import slick.jdbc.JdbcActionComponent
import slick.jdbc.SQLiteProfile.api._

trait SlickEscapes {
  implicit def tableElementEscape[E, T <: Table[E]](what: T#TableElementType): E =
    what.asInstanceOf[E]

  private def condenseName(str: String): String = str.split('.').toList match {
    case name :: Nil => name
    case xs @ _ :: _ =>
      val (init, last) = (xs.init, xs.last)
      init.map(_.take(1)).mkString(start = "", sep = ".", end = ".") + last
    case Nil         => ""
  }

  implicit class RichStatements[R, T](
    result: JdbcActionComponent#StreamingProfileAction[R, T, Effect.Read],
  ) {
    def withStatements(
      clazz: Class[_],
    )(implicit verbose: Boolean): JdbcActionComponent#StreamingProfileAction[R, T, Effect.Read] = {
      if (verbose) {
        result.statements.foreach { statement =>
          println(s"[${condenseName(clazz.getName)}] Executing: $statement")
        }
      }

      result
    }
  }

  implicit class RichDBIOAction[+R <: Iterable[_], +S <: NoStream, -E <: Effect](
    action: DBIOAction[R, S, E],
  ) {
    def withCounting(
      clazz: Class[_],
    )(implicit ec: ExecutionContext, verbose: Boolean): DBIOAction[R, NoStream, E] =
      action.map { iter =>
        if (verbose) {
          val len = iter.size
          val rs = if (len == 1) "result" else "results"

          println(s"[${condenseName(clazz.getName)}] Got ${iter.size} $rs.")
        }

        iter
      }
  }
}
