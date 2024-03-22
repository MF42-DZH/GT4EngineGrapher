package gt4enginegrapher.wrappers

import gt4enginegrapher.schema.{Engine, SimpleEngine}

class EngineGraph(engine: SimpleEngine) {
  lazy val points: Seq[(Int, (BigDecimal, BigDecimal))] =
    (engine.idleRpm to engine.revLimit by 10).map { rpm =>
      (
        rpm,
        (
          engine.torqueAt(rpm).getOrElse(BigDecimal(0)),
          engine.powerAt(rpm).getOrElse(BigDecimal(0)),
        ),
      )
    }
}

object EngineGraph {
  def apply(engine: Engine): EngineGraph = new EngineGraph(engine.toSimpleEngine)
  def apply(engine: SimpleEngine): EngineGraph = new EngineGraph(engine)
}
