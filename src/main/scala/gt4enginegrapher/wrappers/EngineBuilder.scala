package gt4enginegrapher.wrappers

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

import gt4enginegrapher.schema._
import slick.jdbc.JdbcBackend.JdbcDatabaseDef
import slick.jdbc.SQLiteProfile.api._

abstract class EngineBuilder private[wrappers] () {
  val name: SimpleName
  val engine: Engine

  val polish: Option[PortPolish]
  val balance: Option[EngineBalance]
  val displacment: Option[DisplacementUp]
  val computer: Option[Computer]
  val natune: Seq[NATune]
  val turbine: Seq[TurbineKit]
  val muffler: Seq[Muffler]
  val intercooler: Seq[Intercooler]
  val supercharger: Option[Supercharger]
  val nos: Option[Nitrous]

  var chosenPolish: Option[PortPolish] = None
  var chosenBalance: Option[EngineBalance] = None
  var chosenDisplacment: Option[DisplacementUp] = None
  var chosenComputer: Option[Computer] = None
  var chosenNatune: Option[NATune] = None
  var chosenTurbine: Option[TurbineKit] = None
  var chosenMuffler: Option[Muffler] = None
  var chosenIntercooler: Option[Intercooler] = None
  var chosenSupercharger: Option[Supercharger] = None
  var chosenNitrousSetting: Option[Int] = None

  def buildStockEngine(): (SimpleName, SimpleEngine) = (name, engine.toSimpleEngine)

  def buildEngine(): (SimpleName, SimpleEngine) = (for {
    chosenEngine     <- Some(engine.toSimpleEngine)
    withPP           <- chosenPolish.map(_.remapEngine(chosenEngine)).orElse(Some(chosenEngine))
    withBal          <- chosenBalance
      .map(eb => (eb.remapRevs compose eb.remapEngine)(withPP))
      .orElse(Some(withPP))
    withDisp         <- chosenDisplacment.map(_.remapEngine(withBal)).orElse(Some(withBal))
    withComp         <- chosenComputer.map(_.remapEngine(withDisp)).orElse(Some(withDisp))
    withNatune       <- chosenNatune
      .map(nat => (nat.remapRevs compose nat.remapEngine)(withComp))
      .orElse(Some(withComp))
    withTurbine      <- chosenTurbine
      .map(tk => (tk.remapRevs compose tk.remapEngine)(withNatune))
      .orElse(Some(withNatune))
    withMuffler      <- chosenMuffler.map(_.remapEngine(withTurbine)).orElse(Some(withTurbine))
    withIntercooler  <- chosenIntercooler.map(_.remapEngine(withMuffler)).orElse(Some(withMuffler))
    withSupercharger <- chosenSupercharger
      .map(_.remapEngine(withIntercooler))
      .orElse(Some(withIntercooler))
    withNOS          <- nos
      .flatMap(n => chosenNitrousSetting.flatMap(ns => n.toUsedNitrous(ns)))
      .map(_.remapEngine(withSupercharger))
      .orElse(Some(withSupercharger))
  } yield (name, withNOS)).getOrElse(buildStockEngine())
}

object EngineBuilder {
  def createForName(
    lookFor: SimpleName,
  )(implicit schema: AllSchema, db: JdbcDatabaseDef, ec: ExecutionContext): EngineBuilder = {
    import schema._

    def byLabel[T <: SpecTable[_]](table: TableQuery[T]): Future[Seq[T#TableElementType]] =
      db.run {
        table
          .filter(_.label.like(s"%${lookFor.label}%"))
          .result
      }

    Await.result(
      for {
        fEngine       <- byLabel(engines).map(_.head)
        fPolish       <- byLabel(portPolishes).map(_.headOption)
        fBalance      <- byLabel(engineBalances).map(_.headOption)
        fDisp         <- byLabel(displacementUps).map(_.headOption)
        fComputer     <- byLabel(computers).map(_.headOption)
        fNaTune       <- byLabel(naTunes)
        fTurbine      <- byLabel(turbineKits)
        fMuffler      <- byLabel(mufflers)
        fIntercooler  <- byLabel(intercoolers)
        fSupercharger <- byLabel(superchargers).map(_.headOption)
        fNos          <- byLabel(nitrouses).map(_.headOption)
      } yield new EngineBuilder {
        override val name: SimpleName = lookFor
        override val engine: Engine = fEngine

        override val polish: Option[PortPolish] = fPolish
        override val balance: Option[EngineBalance] = fBalance
        override val displacment: Option[DisplacementUp] = fDisp
        override val computer: Option[Computer] = fComputer
        override val natune: Seq[NATune] = fNaTune
        override val turbine: Seq[TurbineKit] = fTurbine
        override val muffler: Seq[Muffler] = fMuffler
        override val intercooler: Seq[Intercooler] = fIntercooler
        override val supercharger: Option[Supercharger] = fSupercharger
        override val nos: Option[Nitrous] = fNos
      },
      Duration.Inf,
    )
  }
}
