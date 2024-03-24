package gt4enginegrapher.schema

import scala.util.Try

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

case class Engine(
  rowId: Int,
  label: String,
  displacement: String,
  engineType: String,
  cam: String,
  aspiration: String,
  psRpm: String,
  torqueRpm: String,
  soundNum: Int,
  psValue: Int,
  torqueValue: Int,
  torqueA: Int,
  torqueB: Int,
  torqueC: Int,
  torqueD: Int,
  torqueE: Int,
  torqueF: Int,
  torqueG: Int,
  torqueH: Int,
  torqueI: Int,
  torqueJ: Int,
  torqueK: Int,
  torqueL: Int,
  torqueM: Int,
  torqueN: Int,
  torqueO: Int,
  torqueP: Int,
  torqueQ: Int,
  torqueR: Int,
  torqueS: Int,
  torqueT: Int,
  torqueU: Int,
  torqueV: Int,
  torqueW: Int,
  torqueX: Int,
  category: Int,
  dpsFlag: Int,
  shiftLimit: Int,
  revLimit: Int,
  unk: Int,
  clutchMeetRpm: Int,
  torquePoint: Int,
  rpmA: Int,
  rpmB: Int,
  rpmC: Int,
  rpmD: Int,
  rpmE: Int,
  rpmF: Int,
  rpmG: Int,
  rpmH: Int,
  rpmI: Int,
  rpmJ: Int,
  rpmK: Int,
  rpmL: Int,
  rpmM: Int,
  rpmN: Int,
  rpmO: Int,
  rpmP: Int,
  rpmQ: Int,
  rpmR: Int,
  rpmS: Int,
  rpmT: Int,
  rpmU: Int,
  rpmV: Int,
  rpmW: Int,
  rpmX: Int,
  redLine: Int,
  meterScale: Int,
  torqueVol: Int,
  gasConsumptionRate: Int,
) {
  private def scaleRpm(rpm: Int): Int = rpm * 100
  private def scaleTorque(torque: Int): BigDecimal = BigDecimal(torque) / BigDecimal(100)

  lazy val toSimpleEngine: SimpleEngine = SimpleEngine(
    label        = label,
    torquePoints = Seq(
      (scaleTorque(torqueA), scaleRpm(rpmA)),
      (scaleTorque(torqueB), scaleRpm(rpmB)),
      (scaleTorque(torqueC), scaleRpm(rpmC)),
      (scaleTorque(torqueD), scaleRpm(rpmD)),
      (scaleTorque(torqueE), scaleRpm(rpmE)),
      (scaleTorque(torqueF), scaleRpm(rpmF)),
      (scaleTorque(torqueG), scaleRpm(rpmG)),
      (scaleTorque(torqueH), scaleRpm(rpmH)),
      (scaleTorque(torqueI), scaleRpm(rpmI)),
      (scaleTorque(torqueJ), scaleRpm(rpmJ)),
      (scaleTorque(torqueK), scaleRpm(rpmK)),
      (scaleTorque(torqueL), scaleRpm(rpmL)),
      (scaleTorque(torqueM), scaleRpm(rpmM)),
      (scaleTorque(torqueN), scaleRpm(rpmN)),
      (scaleTorque(torqueO), scaleRpm(rpmO)),
      (scaleTorque(torqueP), scaleRpm(rpmP)),
      (scaleTorque(torqueQ), scaleRpm(rpmQ)),
      (scaleTorque(torqueR), scaleRpm(rpmR)),
      (scaleTorque(torqueS), scaleRpm(rpmS)),
      (scaleTorque(torqueT), scaleRpm(rpmT)),
      (scaleTorque(torqueU), scaleRpm(rpmU)),
      (scaleTorque(torqueV), scaleRpm(rpmV)),
      (scaleTorque(torqueW), scaleRpm(rpmW)),
      (scaleTorque(torqueX), scaleRpm(rpmX)),
    ).filter(_._2 > 0),
    torqueVol    = scaleTorque(torqueVol),
    redLine      = scaleRpm(redLine),
    revLimit     = scaleRpm(revLimit),
  )
}

object Engine {
  def ofTuple(
    rowId: Int,
    label: String,
    displayInfo: (String, String, String, String, String, String),
    soundNum: Int,
    psValue: Int,
    torqueValue: Int,
    torquePointsAL: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
    torquePointsMX: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
    category: Int,
    dpsFlag: Int,
    internalGearing: (Int, Int, Int, Int),
    torquePoint: Int,
    rpmPointsAL: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
    rpmPointsMX: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
    redLine: Int,
    meterScale: Int,
    torqueVol: Int,
    gasConsumptionRate: Int,
  ): Engine = Engine(
    rowId              = rowId,
    label              = label,
    displacement       = displayInfo._1,
    engineType         = displayInfo._2,
    cam                = displayInfo._3,
    aspiration         = displayInfo._4,
    psRpm              = displayInfo._5,
    torqueRpm          = displayInfo._6,
    soundNum           = soundNum,
    psValue            = psValue,
    torqueValue        = torqueValue,
    torqueA            = torquePointsAL._1,
    torqueB            = torquePointsAL._2,
    torqueC            = torquePointsAL._3,
    torqueD            = torquePointsAL._4,
    torqueE            = torquePointsAL._5,
    torqueF            = torquePointsAL._6,
    torqueG            = torquePointsAL._7,
    torqueH            = torquePointsAL._8,
    torqueI            = torquePointsAL._9,
    torqueJ            = torquePointsAL._10,
    torqueK            = torquePointsAL._11,
    torqueL            = torquePointsAL._12,
    torqueM            = torquePointsMX._1,
    torqueN            = torquePointsMX._2,
    torqueO            = torquePointsMX._3,
    torqueP            = torquePointsMX._4,
    torqueQ            = torquePointsMX._5,
    torqueR            = torquePointsMX._6,
    torqueS            = torquePointsMX._7,
    torqueT            = torquePointsMX._8,
    torqueU            = torquePointsMX._9,
    torqueV            = torquePointsMX._10,
    torqueW            = torquePointsMX._11,
    torqueX            = torquePointsMX._12,
    category           = category,
    dpsFlag            = dpsFlag,
    shiftLimit         = internalGearing._1,
    revLimit           = internalGearing._2,
    unk                = internalGearing._3,
    clutchMeetRpm      = internalGearing._4,
    torquePoint        = torquePoint,
    rpmA               = rpmPointsAL._1,
    rpmB               = rpmPointsAL._2,
    rpmC               = rpmPointsAL._3,
    rpmD               = rpmPointsAL._4,
    rpmE               = rpmPointsAL._5,
    rpmF               = rpmPointsAL._6,
    rpmG               = rpmPointsAL._7,
    rpmH               = rpmPointsAL._8,
    rpmI               = rpmPointsAL._9,
    rpmJ               = rpmPointsAL._10,
    rpmK               = rpmPointsAL._11,
    rpmL               = rpmPointsAL._12,
    rpmM               = rpmPointsMX._1,
    rpmN               = rpmPointsMX._2,
    rpmO               = rpmPointsMX._3,
    rpmP               = rpmPointsMX._4,
    rpmQ               = rpmPointsMX._5,
    rpmR               = rpmPointsMX._6,
    rpmS               = rpmPointsMX._7,
    rpmT               = rpmPointsMX._8,
    rpmU               = rpmPointsMX._9,
    rpmV               = rpmPointsMX._10,
    rpmW               = rpmPointsMX._11,
    rpmX               = rpmPointsMX._12,
    redLine            = redLine,
    meterScale         = meterScale,
    torqueVol          = torqueVol,
    gasConsumptionRate = gasConsumptionRate,
  )

  def destructure(engine: Engine): Some[
    (
      Int,
      String,
      (String, String, String, String, String, String),
      Int,
      Int,
      Int,
      (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
      (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
      Int,
      Int,
      (Int, Int, Int, Int),
      Int,
      (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
      (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
      Int,
      Int,
      Int,
      Int,
    ),
  ] = engine match {
    case Engine(
        rowId,
        label,
        displacement,
        engineType,
        cam,
        aspiration,
        psRpm,
        torqueRpm,
        soundNum,
        psValue,
        torqueValue,
        torqueA,
        torqueB,
        torqueC,
        torqueD,
        torqueE,
        torqueF,
        torqueG,
        torqueH,
        torqueI,
        torqueJ,
        torqueK,
        torqueL,
        torqueM,
        torqueN,
        torqueO,
        torqueP,
        torqueQ,
        torqueR,
        torqueS,
        torqueT,
        torqueU,
        torqueV,
        torqueW,
        torqueX,
        category,
        dpsFlag,
        shiftLimit,
        revLimit,
        unk,
        clutchMeetRpm,
        torquePoint,
        rpmA,
        rpmB,
        rpmC,
        rpmD,
        rpmE,
        rpmF,
        rpmG,
        rpmH,
        rpmI,
        rpmJ,
        rpmK,
        rpmL,
        rpmM,
        rpmN,
        rpmO,
        rpmP,
        rpmQ,
        rpmR,
        rpmS,
        rpmT,
        rpmU,
        rpmV,
        rpmW,
        rpmX,
        redLine,
        meterScale,
        torqueVol,
        gasConsumptionRate,
      ) =>
      Some(
        (
          rowId,
          label,
          (displacement, engineType, cam, aspiration, psRpm, torqueRpm),
          soundNum,
          psValue,
          torqueValue,
          (
            torqueA,
            torqueB,
            torqueC,
            torqueD,
            torqueE,
            torqueF,
            torqueG,
            torqueH,
            torqueI,
            torqueJ,
            torqueK,
            torqueL,
          ),
          (
            torqueM,
            torqueN,
            torqueO,
            torqueP,
            torqueQ,
            torqueR,
            torqueS,
            torqueT,
            torqueU,
            torqueV,
            torqueW,
            torqueX,
          ),
          category,
          dpsFlag,
          (shiftLimit, revLimit, unk, clutchMeetRpm),
          torquePoint,
          (
            rpmA,
            rpmB,
            rpmC,
            rpmD,
            rpmE,
            rpmF,
            rpmG,
            rpmH,
            rpmI,
            rpmJ,
            rpmK,
            rpmL,
          ),
          (
            rpmM,
            rpmN,
            rpmO,
            rpmP,
            rpmQ,
            rpmR,
            rpmS,
            rpmT,
            rpmU,
            rpmV,
            rpmW,
            rpmX,
          ),
          redLine,
          meterScale,
          torqueVol,
          gasConsumptionRate,
        ),
      )
  }
}

case class SimpleEngine(
  label: String,
  torquePoints: Seq[(BigDecimal, Int)],
  torqueVol: BigDecimal,
  redLine: Int,
  revLimit: Int,
) {
  import SimpleEngine.TurboStats

  // Converts (kgf.m, RPM) => PS
  final private val torqueToPowerConstant: BigDecimal = BigDecimal("716.2")
  final private val atmosphere: BigDecimal = BigDecimal("1.01325")

  def idleRpm: Int = torquePoints.head._2

  def rawTorqueAt(rpm: Int): Option[BigDecimal] =
    torquePoints
      .zip(torquePoints.tail)
      .find { case ((_, r1), (_, r2)) =>
        rpm >= r1 && rpm <= r2
      }
      .map { case ((t1, r1), (t2, r2)) =>
        val t = BigDecimal(rpm - r1) / BigDecimal(r2 - r1)
        (BigDecimal(1) - t) * t1 + t * t2
      }

  def torqueAt(rpm: Int): Option[BigDecimal] = rawTorqueAt(rpm).map(_ * torqueVol)

  def rawPowerAt(rpm: Int): Option[BigDecimal] =
    rawTorqueAt(rpm).map(_ * BigDecimal(rpm) / torqueToPowerConstant)

  def powerAt(rpm: Int): Option[BigDecimal] =
    torqueAt(rpm).map(_ * BigDecimal(rpm) / torqueToPowerConstant)

  private def unlerp(lower: Int, upper: Int, where: Int): BigDecimal =
    BigDecimal(where - lower) / BigDecimal(upper - lower).abs

  private def lerp(lower: BigDecimal, upper: BigDecimal, where: BigDecimal): BigDecimal =
    (BigDecimal(1) - where) * lower + where * upper

  def remapTurbo(
    turboStatsLow: TurboStats,
    turboStatsHigh: TurboStats,
  ): SimpleEngine =
    SimpleEngine(
      label        = label,
      torquePoints = torquePoints.map { case (t, rpm) =>
        (turboStatsLow, turboStatsHigh) match {
          case (
              TurboStats(mod1, boost1, peak1, response1),
              TurboStats(mod2, boost2, peak2, response2),
            ) =>
            val where1 = unlerp(idleRpm, peak1 + response1, rpm)
            val where2 =
              Try(unlerp(peak1, (peak2 + response2).max(redLine), rpm))
                .getOrElse(where1)

            val umod1 =
              if (where1 < 0) BigDecimal(1)
              else if (where1 > 1) mod1
              else lerp(BigDecimal(1), mod1, where1)
            val umod2 =
              if (where2 < 0) BigDecimal(1)
              else if (where2 > 1) mod2
              else lerp(BigDecimal(1), mod2, where2)

            val b1 =
              if (where1 < 0) BigDecimal(0)
              else if (where1 > 1) boost1
              else lerp(BigDecimal(0), boost1, where1)
            val b2 =
              if (where2 < 0) BigDecimal(0)
              else if (where2 > 1) boost2
              else lerp(BigDecimal(0), boost2, where1)

            val bmod1 = (b1 + atmosphere) / atmosphere
            val bmod2 = (b2 + atmosphere) / atmosphere

            (t * umod1 * umod2 * bmod1 * bmod2, rpm)
        }
      },
      torqueVol    = torqueVol,
      redLine      = redLine,
      revLimit     = revLimit,
    )
}

object SimpleEngine {
  case class TurboStats(
    torqueModifier: BigDecimal,
    boost: BigDecimal,
    peakRpm: Int,
    response: Int,
  )
}

trait EngineProvider {
  class EngineT(tag: Tag) extends Table[Engine](tag, "ENGINE") {
    def rowId = column[Int]("RowId")
    def label = column[String]("Label")
    def displacement = column[String]("discplacement")
    def engineType = column[String]("enginetype")
    def cam = column[String]("cam")
    def aspiration = column[String]("aspiration")
    def psRpm = column[String]("psrpm")
    def torqueRpm = column[String]("torquerpm")
    def soundNum = column[Int]("soundNum")
    def psValue = column[Int]("psvalue")
    def torqueValue = column[Int]("torquevalue")
    def torqueA = column[Int]("torqueA")
    def torqueB = column[Int]("torqueB")
    def torqueC = column[Int]("torqueC")
    def torqueD = column[Int]("torqueD")
    def torqueE = column[Int]("torqueE")
    def torqueF = column[Int]("torqueF")
    def torqueG = column[Int]("torqueG")
    def torqueH = column[Int]("torqueH")
    def torqueI = column[Int]("torqueI")
    def torqueJ = column[Int]("torqueJ")
    def torqueK = column[Int]("torqueK")
    def torqueL = column[Int]("torqueL")
    def torqueM = column[Int]("torqueM")
    def torqueN = column[Int]("torqueN")
    def torqueO = column[Int]("torqueO")
    def torqueP = column[Int]("torqueP")
    def torqueQ = column[Int]("torqueQ")
    def torqueR = column[Int]("torqueR")
    def torqueS = column[Int]("torqueS")
    def torqueT = column[Int]("torqueT")
    def torqueU = column[Int]("torqueU")
    def torqueV = column[Int]("torqueV")
    def torqueW = column[Int]("torqueW")
    def torqueX = column[Int]("torqueX")
    def category = column[Int]("category")
    def dpsFlag = column[Int]("dpsflag")
    def shiftLimit = column[Int]("shiftlimit")
    def revLimit = column[Int]("revlimit")
    def unk = column[Int]("Unk")
    def clutchMeetRpm = column[Int]("clutchmeetrpm")
    def torquePoint = column[Int]("torquepoint")
    def rpmA = column[Int]("rpmA")
    def rpmB = column[Int]("rpmB")
    def rpmC = column[Int]("rpmC")
    def rpmD = column[Int]("rpmD")
    def rpmE = column[Int]("rpmE")
    def rpmF = column[Int]("rpmF")
    def rpmG = column[Int]("rpmG")
    def rpmH = column[Int]("rpmH")
    def rpmI = column[Int]("rpmI")
    def rpmJ = column[Int]("rpmJ")
    def rpmK = column[Int]("rpmK")
    def rpmL = column[Int]("rpmL")
    def rpmM = column[Int]("rpmM")
    def rpmN = column[Int]("rpmN")
    def rpmO = column[Int]("rpmO")
    def rpmP = column[Int]("rpmP")
    def rpmQ = column[Int]("rpmQ")
    def rpmR = column[Int]("rpmR")
    def rpmS = column[Int]("rpmS")
    def rpmT = column[Int]("rpmT")
    def rpmU = column[Int]("rpmU")
    def rpmV = column[Int]("rpmV")
    def rpmW = column[Int]("rpmW")
    def rpmX = column[Int]("rpmX")
    def redLine = column[Int]("RedLine")
    def meterScale = column[Int]("MeterScale")
    def torqueVol = column[Int]("torquevol")
    def gasConsumptionRate = column[Int]("GasConsumptionRate")

    override def * : ProvenShape[Engine] =
      (
        rowId,
        label,
        (displacement, engineType, cam, aspiration, psRpm, torqueRpm),
        soundNum,
        psValue,
        torqueValue,
        (
          torqueA,
          torqueB,
          torqueC,
          torqueD,
          torqueE,
          torqueF,
          torqueG,
          torqueH,
          torqueI,
          torqueJ,
          torqueK,
          torqueL,
        ),
        (
          torqueM,
          torqueN,
          torqueO,
          torqueP,
          torqueQ,
          torqueR,
          torqueS,
          torqueT,
          torqueU,
          torqueV,
          torqueW,
          torqueX,
        ),
        category,
        dpsFlag,
        (shiftLimit, revLimit, unk, clutchMeetRpm),
        torquePoint,
        (
          rpmA,
          rpmB,
          rpmC,
          rpmD,
          rpmE,
          rpmF,
          rpmG,
          rpmH,
          rpmI,
          rpmJ,
          rpmK,
          rpmL,
        ),
        (
          rpmM,
          rpmN,
          rpmO,
          rpmP,
          rpmQ,
          rpmR,
          rpmS,
          rpmT,
          rpmU,
          rpmV,
          rpmW,
          rpmX,
        ),
        redLine,
        meterScale,
        torqueVol,
        gasConsumptionRate,
      ) <> ((Engine.ofTuple _).tupled, Engine.destructure)
  }

  lazy val engines = TableQuery[EngineT]
}
