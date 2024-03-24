package gt4enginegrapher.wrappers

import gt4enginegrapher.schema._

class EngineBuilder(val name: SimpleName, val engine: Engine) {
  var chosenPolish: Option[PortPolish] = None
  var chosenBalance: Option[EngineBalance] = None
  var chosenDisplacment: Option[DisplacementUp] = None
  var chosenComputer: Option[Computer] = None
  var chosenNaTune: Option[NATune] = None
  var chosenTurbine: Option[TurbineKit] = None
  var chosenMuffler: Option[Muffler] = None
  var chosenIntercooler: Option[Intercooler] = None
  var chosenSupercharger: Option[Supercharger] = None
  var chosenNos: Option[Nitrous] = None
  var chosenNitrousSetting: Option[Int] = None

  def buildStockEngine(): (SimpleName, SimpleEngine) = (name, engine.toSimpleEngine)

  def buildEngine(): (SimpleName, SimpleEngine) = (for {
    chosenEngine     <- Some(engine.toSimpleEngine)
    withPP           <- chosenPolish.map(_.remapEngine(chosenEngine)).orElse(Some(chosenEngine))
    withBal          <- chosenBalance
      .map(eb => ((eb.remapRevs _) compose (eb.remapEngine _))(withPP))
      .orElse(Some(withPP))
    withDisp         <- chosenDisplacment.map(_.remapEngine(withBal)).orElse(Some(withBal))
    withComp         <- chosenComputer.map(_.remapEngine(withDisp)).orElse(Some(withDisp))
    withNatune       <- chosenNaTune
      .map(nat => ((nat.remapRevs _) compose (nat.remapEngine _))(withComp))
      .orElse(Some(withComp))
    withTurbine      <- chosenTurbine
      .map(tk => ((tk.remapRevs _) compose (tk.remapEngine _))(withNatune))
      .orElse(Some(withNatune))
    withMuffler      <- chosenMuffler.map(_.remapEngine(withTurbine)).orElse(Some(withTurbine))
    withIntercooler  <- chosenIntercooler.map(_.remapEngine(withMuffler)).orElse(Some(withMuffler))
    withSupercharger <- chosenSupercharger
      .map(_.remapEngine(withIntercooler))
      .orElse(Some(withIntercooler))
    withNOS          <- chosenNos
      .flatMap(n => chosenNitrousSetting.flatMap(ns => n.toUsedNitrous(ns)))
      .map(_.remapEngine(withSupercharger))
      .orElse(Some(withSupercharger))
  } yield (name, withNOS)).getOrElse(buildStockEngine())
}
