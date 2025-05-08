package gtenginegrapher.wrappers

import gtenginegrapher.schema.{Engine, SimpleEngine}

class EngineGraph(engine: SimpleEngine) {
  lazy val points: Seq[(Int, (BigDecimal, BigDecimal))] =
    (engine.idleRpm to engine.torquePoints.map(_._2).max by 10).map { rpm =>
      (
        rpm,
        (
          engine.torqueAt(rpm).getOrElse(BigDecimal(0)),
          engine.powerAt(rpm).getOrElse(BigDecimal(0)),
        ),
      )
    }

  lazy val peakTorque: (Int, BigDecimal) = {
    val max = points.filter(_._1 <= engine.revLimit).maxBy(p => (p._2._1, -p._1))
    (max._1, max._2._1)
  }

  lazy val peakPower: (Int, BigDecimal) = {
    val max = points.filter(_._1 <= engine.revLimit).maxBy(p => (p._2._2, p._1))
    (max._1, max._2._2)
  }
}

object EngineGraph {
  def apply(engine: Engine): EngineGraph = new EngineGraph(engine.toSimpleEngine)
  def apply(engine: SimpleEngine): EngineGraph = new EngineGraph(engine)
}
