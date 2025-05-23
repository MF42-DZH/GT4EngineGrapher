package gtenginegrapher.utils

trait ConfigKey {
  def key: String = super.toString.trim.toLowerCase

  def displayString: String = key
  override def toString: String = displayString
}

abstract class ConfigKeys extends Enumeration {
  protected class ConfigKeyVal extends super.Val with ConfigKey

  type KeyVal <: ConfigKeyVal

  import scala.language.implicitConversions
  implicit def valueToKey(x: Value): KeyVal = x.asInstanceOf[KeyVal]

  def keys: Set[this.type#KeyVal] = values.map(_.asInstanceOf[KeyVal])

  def tryParse(from: String): Option[KeyVal] = {
    val clean = from.trim.toLowerCase
    values.find(_.key == clean).map(_.asInstanceOf[KeyVal])
  }
}
