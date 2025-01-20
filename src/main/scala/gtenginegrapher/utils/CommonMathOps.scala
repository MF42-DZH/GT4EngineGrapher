package gtenginegrapher.utils

trait CommonMathOps {
  final def lerp(lower: BigDecimal, upper: BigDecimal, where: BigDecimal): BigDecimal =
    lower + (where * (upper - lower))

  implicit class IntExtensions(n: Int) {
    def bd: BigDecimal = BigDecimal(n)
  }

  implicit class RichBigDecimalOps(bd: BigDecimal) {
    def twoDp: BigDecimal = bd.setScale(2, BigDecimal.RoundingMode.DOWN)
  }
}
