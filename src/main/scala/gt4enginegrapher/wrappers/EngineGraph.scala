package gt4enginegrapher.wrappers

import gt4enginegrapher.schema.{Engine, SimpleEngine}

class EngineGraph(engine: SimpleEngine) {
  implicit private class Rounding(bd: BigDecimal) {
    def rtt: BigDecimal = bd.setScale(2, BigDecimal.RoundingMode.DOWN)
  }

  lazy val points: Seq[(Int, (BigDecimal, BigDecimal))] =
    (engine.idleRpm to engine.torquePoints.map(_._2).max by 10).map { rpm =>
      (
        rpm,
        (
          engine.torqueAt(rpm).getOrElse(BigDecimal(0)).rtt,
          engine.powerAt(rpm).getOrElse(BigDecimal(0)).rtt,
        ),
      )
    }

  lazy val peakTorque: (Int, BigDecimal) = {
    val max = points.filter(_._1 < engine.revLimit).maxBy(p => (p._2._1, -p._1))
    (max._1, max._2._1)
  }

  lazy val peakPower: (Int, BigDecimal) = {
    val max = points.filter(_._1 < engine.revLimit).maxBy(p => (p._2._2, p._1))
    (max._1, max._2._2)
  }

  println(
    s"""PEAK TORQUE: $peakTorque -> ${engine.powerAt(peakTorque._1)}
       | PEAK POWER: $peakPower -> ${engine.torqueAt(peakPower._1)}
       |""".stripMargin)
}

object EngineGraph {
  def apply(engine: Engine): EngineGraph = new EngineGraph(engine.toSimpleEngine)
  def apply(engine: SimpleEngine): EngineGraph = new EngineGraph(engine)
}
