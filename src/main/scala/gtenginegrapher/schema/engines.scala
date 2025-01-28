package gtenginegrapher.schema

import gtenginegrapher.utils.CommonMathOps
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape

// It really feels like this table should've been split into several tables by Polyphony.
// One containing the engine stats, the other containing the torque points.
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
    label                 = label,
    torquePoints          = Seq(
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
    highRPMTorqueModifier = scaleTorque(torqueVol),
    lowRPMTorqueModifier  = scaleTorque(torqueVol),
    redLine               = scaleRpm(redLine),
    shiftLimit            = scaleRpm(shiftLimit),
    revLimit              = scaleRpm(revLimit),
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
  highRPMTorqueModifier: BigDecimal,
  lowRPMTorqueModifier: BigDecimal,
  redLine: Int,
  shiftLimit: Int,
  revLimit: Int,
) extends CommonMathOps {
  // Converts (kgf.m, RPM) => PS
  final private val torqueToPowerConstant: BigDecimal = BigDecimal("716.2")

  def idleRpm: Int = torquePoints.head._2

  def rawTorqueAt(rpm: Int): Option[BigDecimal] =
    torquePoints
      .zip(torquePoints.tail)
      .find { case ((_, r1), (_, r2)) =>
        rpm >= r1 && rpm <= r2
      }
      .map { case ((t1, r1), (t2, r2)) =>
        val t = unlerpRPM(r1, r2, rpm)
        lerp(t1, t2, t)
      }

  def torqueAt(rpm: Int): Option[BigDecimal] = rawTorqueAt(rpm).map { torque =>
    val where =
      unlerpRPM(torquePoints.minBy(_._2)._2, torquePoints.maxBy(_._2)._2.min(revLimit), rpm)
        .min(BigDecimal(1))
    val umod = lerp(lowRPMTorqueModifier, highRPMTorqueModifier, where)
    val total = torque * umod
    total
  }

  def powerAt(rpm: Int): Option[BigDecimal] =
    torqueAt(rpm).map { torque =>
      // XXX: For some reason, GT4 adds 0.5 PS to the displayed power of each car???
      (torque * BigDecimal(rpm) / torqueToPowerConstant) + BigDecimal("0.5")
    }

  private def unlerpRPM(lower: Int, upper: Int, where: Int): BigDecimal =
    (BigDecimal(where - lower) / BigDecimal(upper - lower)).abs

  def remapEngine(
    mod1: BigDecimal,
    mod2: BigDecimal,
  ): SimpleEngine =
    SimpleEngine(
      label                 = label,
      torquePoints          = torquePoints,
      highRPMTorqueModifier = highRPMTorqueModifier * mod1,
      lowRPMTorqueModifier  = lowRPMTorqueModifier * mod2,
      redLine               = redLine,
      shiftLimit            = shiftLimit,
      revLimit              = revLimit,
    )
}

trait GT4EngineProvider {
  class EngineT(tag: Tag) extends GT4SpecTable[Engine](tag, "ENGINE") {
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

trait GT3EngineProvider {
  class EngineT(tag: Tag) extends GT3SpecTable[Engine](tag, "ENGINE") {
    def displacement = column[String]("displacement")
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
    def shiftLimit = column[Int]("shiftlimit")
    def revLimit = column[Int]("revlimit")
    def unk = column[Int]("idlerpm")
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
    def redLine = column[Int]("shiftlimit")
    def torqueVol = column[Int]("torquemul")

    override def * : ProvenShape[Engine] =
      (
        LiteralColumn(0),
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
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
        ),
        LiteralColumn(0),
        LiteralColumn(0),
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
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
          LiteralColumn(0),
        ),
        redLine,
        LiteralColumn(0),
        torqueVol,
        LiteralColumn(0),
      ) <> ((Engine.ofTuple _).tupled, Engine.destructure)
  }

  lazy val engines = TableQuery[EngineT]
}
