package gtenginegrapher.utils

sealed trait Region { def label: String; override lazy val toString: String = label }
case object NtscU extends Region { override val label: String = "USA" }
case object NtscK extends Region { override val label: String = "Korea" }
case object NtscJ extends Region { override val label: String = "Japan" }
case object Pal extends Region { override val label: String = "Europe" }
