package gtenginegrapher.utils

object TorqueUnits extends ConfigKeys {
  protected case class Unit(multiplier: BigDecimal, private val display: String)
    extends ConfigKeyVal {
    override val displayString: String = display
  }

  override type KeyVal = Unit

  val Kgfm: Unit = Unit(1.0, "kgf.m")
  val Nm: Unit = Unit(BigDecimal("9.80665"), "Nm")
  val Lbfft: Unit = Unit(BigDecimal("7.233002906"), "lbf.ft")
}

object PowerUnits extends ConfigKeys {
  protected case class Unit(multiplier: BigDecimal, private val display: String)
    extends ConfigKeyVal {
    override val displayString: String = display
  }

  override type KeyVal = Unit

  val Ps: Unit = Unit(1.0, "PS")
  val Cv: Unit = Unit(1.0, "CV")
  val Kw: Unit = Unit(BigDecimal("0.73549875"), "kW")
  val Hp: Unit = Unit(BigDecimal("0.9863200706"), "HP")
  val Bhp: Unit = Unit(BigDecimal("0.9863200706"), "BHP")
}
