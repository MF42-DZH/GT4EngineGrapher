package gtenginegrapher.wrappers

import gtenginegrapher.utils.CommonMathOps

/** The way engine and oil wear works seems to slightly change between Gran Turismo 3 and 4 This
  * trait encodes a common interface between distance travelled and modifiers given by them.
  */
sealed trait WearValues extends CommonMathOps {
  // TODO: Have a UI toggle that can be ticked once the user has changed oil.
  //       If unticked, treat as if the oil is normal (GT4) or dirty (GT3).
  def oilMultiplier(kmSinceOilChange: Int, hasBeenChanged: Boolean): BigDecimal

  final val asBase: WearValues = this
}

sealed trait WearAffectedByPrizeStatus extends WearValues {
  def engineMultiplier(kmTotalTravelled: Int, isPrizeCar: Boolean): BigDecimal
}

sealed trait WearUnaffectedByPrizeStatus extends WearValues {
  def engineMultiplier(kmTotalTravelled: Int): BigDecimal
}

case object GT3Wear extends WearAffectedByPrizeStatus {
  // It sounds like oil doesn't get any worse past 300km in GT3.
  override def oilMultiplier(kmSinceOilChange: Int, hasBeenChanged: Boolean): BigDecimal = {
    val usedKmDistance = if (hasBeenChanged) kmSinceOilChange else kmSinceOilChange + 300

    usedKmDistance match {
      case n if n < 200 => BigDecimal("1.05")
      case n if n < 300 => lerp(BigDecimal("1.05"), 1.bd, (n - 200).bd / 100.bd)
      case _            => 1.bd
    }
  }

  //   0-300 km: Break-in period (-3% to ±0%)
  // 300-800 km: Full power period (±0%)
  // 800-900 km: Wear period (±0% to -2%)
  //    901+ km: Full wear period (-2%)
  override def engineMultiplier(kmTotalTravelled: Int, isPrizeCar: Boolean): BigDecimal = {
    // Skip engine break-in period if prize car.
    val usedKmDistance = if (isPrizeCar) kmTotalTravelled + 300 else kmTotalTravelled

    usedKmDistance match {
      case n if n < 300 => lerp(BigDecimal("0.97"), 1.bd, n.bd / 300.bd)
      case n if n < 800 => 1.bd
      case n if n < 900 => lerp(1.bd, BigDecimal("0.98"), (n - 800).bd / 100.bd)
      case _            => BigDecimal("0.98")
    }
  }
}

case object GT4Wear extends WearUnaffectedByPrizeStatus {
  //    0-200 km: Clean Oil (+5%)
  //  200-300 km: Clean->Normal (+5% to ±0%)
  // 300-5300 km: Normal->Dirty (±0% to -5%)
  //    5301+ km: Dirty (-5%)
  override def oilMultiplier(kmSinceOilChange: Int, hasBeenChanged: Boolean): BigDecimal = {
    val usedKmDistance = if (hasBeenChanged) kmSinceOilChange else kmSinceOilChange + 300

    usedKmDistance match {
      case n if n < 200  => BigDecimal("1.05")
      case n if n < 300  => lerp(BigDecimal("1.05"), 1.bd, (n - 200).bd / 100.bd)
      case n if n < 5300 => lerp(1.bd, BigDecimal("0.95"), (n - 300).bd / 5000.bd)
      case _             => BigDecimal("0.95")
    }
  }

  //     0-1000 km: Full performance (±0%)
  // 1000-11000 km: Wear period (±0% to -5%)
  override def engineMultiplier(kmTotalTravelled: Int): BigDecimal = kmTotalTravelled match {
    case n if n < 1000  => 1.bd
    case n if n < 11000 => lerp(1.bd, BigDecimal("0.95"), (n - 1000).bd / 10000.bd)
    case _              => BigDecimal("0.95")
  }
}
