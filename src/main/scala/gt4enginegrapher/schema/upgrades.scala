package gt4enginegrapher.schema

sealed trait Upgrade {
  val category: Int
}

trait CanHaveCarName extends Upgrade {
  var carName: Option[String] = None
  def withCarName(name: String): this.type = {
    carName = Some(name)
    this
  }

  protected def getSuffix: String = carName.map(" (" + _ + ")").getOrElse("")
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
    se.copy(torquePoints = se.torquePoints.map { case (t, r) => (t, r + revLimit * 100) })
}
