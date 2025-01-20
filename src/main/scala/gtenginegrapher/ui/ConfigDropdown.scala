package gtenginegrapher.ui

import javax.swing._

import scala.jdk.CollectionConverters._

import gtenginegrapher.utils.ConfigKeys

class ConfigDropdown[Config <: ConfigKeys](config: Config, default: Config#KeyVal)
  extends JComboBox[AnyRef](
    new java.util.Vector[AnyRef](config.keys.toVector.sortBy(_.id).asJava),
  ) {
  setSelectedItem(default)
}
