package gtenginegrapher.ui

import javax.swing._

import scala.jdk.CollectionConverters._

import gtenginegrapher.utils.ConfigKeys

class ConfigDropdown[Config <: ConfigKeys](config: Config, default: Config#KeyVal)
  extends JComboBox[Config#KeyVal](
    new java.util.Vector[Config#KeyVal](config.keys.toVector.sortBy(_.id).asJava),
  ) {
  setSelectedItem(default)
}
