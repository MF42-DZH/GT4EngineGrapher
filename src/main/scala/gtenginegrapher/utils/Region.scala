package gtenginegrapher.utils

sealed trait Region { def label: String; override lazy val toString: String = label }
case object NtscU extends Region { override val label: String = "USA" }
case object NtscUOpb extends Region { override val label: String = "USA (Online Public Beta)" }
case class Spec2(version: String) extends Region {
  override val label: String = s"Spec II $version"
}
case object NtscK extends Region { override val label: String = "Korea" }
case object NtscJ extends Region { override val label: String = "Japan" }
case object NtscJOpb extends Region { override val label: String = "Japan (Online Public Beta)" }
case object Pal extends Region { override val label: String = "Europe" }
case object Universal extends Region { override val label: String = "Universal" }
