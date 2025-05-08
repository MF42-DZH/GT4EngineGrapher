package gtenginegrapher.schema

sealed trait Upgrade {
  val rowId: Int
  val category: Int
  val price: Int
}

trait CanHaveCarName extends Upgrade {
  var carName: Option[String] = None
  def withCarName(name: String): this.type = {
    carName = Some(name)
    this
  }

  protected def getSuffix: String = {
    val extra = this match {
      case htm: CanHaveCarName with HasTorqueRemapping =>
        s"L ${BigDecimal(htm.lowRPMTorqueModifier) / BigDecimal(100)}x → H ${BigDecimal(htm.highRPMTorqueModifier) / BigDecimal(100)}x"
      case _                                           => ""
    }

    carName
      .map(name => {
        this match {
          case _: CanHaveCarName with HasTorqueRemapping =>
            s" ($name; $extra)"
          case _                                         => s" ($name)"
        }
      })
      .getOrElse(if (extra.isBlank) "" else s" ($extra)")
  }
}

trait HasTorqueRemapping extends Upgrade {
  def highRPMTorqueModifier: Int
  def lowRPMTorqueModifier: Int

  def remapEngine(se: SimpleEngine): SimpleEngine =
    se.remapEngine(
      BigDecimal(highRPMTorqueModifier) / BigDecimal(100),
      BigDecimal(lowRPMTorqueModifier) / BigDecimal(100),
    )
}

trait HasRevIncrease extends Upgrade {
  def shiftLimit: Int
  def revLimit: Int

  def remapRevs(se: SimpleEngine): SimpleEngine =
    se.copy(
      torquePoints = se.torquePoints.map { case (t, r) => (t, r + shiftLimit * 100) },
      revLimit     = se.revLimit + (revLimit * 100),
    )
}
