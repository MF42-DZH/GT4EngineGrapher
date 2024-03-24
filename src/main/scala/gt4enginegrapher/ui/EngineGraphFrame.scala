package gt4enginegrapher.ui

import java.awt.{BasicStroke, Color, Dimension, Font}

import gt4enginegrapher.schema.{Name, SimpleEngine}
import gt4enginegrapher.wrappers.EngineGraph
import org.jfree.chart.{ChartFactory, ChartMouseEvent, ChartMouseListener, ChartPanel, JFreeChart}
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.entity.XYItemEntity
import org.jfree.chart.labels.{
  CrosshairLabelGenerator,
  StandardXYToolTipGenerator,
  XYToolTipGenerator,
}
import org.jfree.chart.panel.CrosshairOverlay
import org.jfree.chart.plot.{Crosshair, XYPlot}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.ui.{ApplicationFrame, RectangleAnchor}
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}

case class EngineGraphFrame(
  private val carName: Name,
  private val engine: SimpleEngine,
) extends ApplicationFrame(s"GT4 Engine Graph")
  with ChartMouseListener {
  private val rawGraphData: EngineGraph = EngineGraph(engine)
  private val torqueC = new XYSeriesCollection
  private val torque = new XYSeries("Torque (kgf.m)")

  private val powerC = new XYSeriesCollection
  private val power = new XYSeries("Power (PS)")

  rawGraphData.points.foreach { case (rpm, (tor, pow)) =>
    torque.add(rpm, tor)
    power.add(rpm, pow)
  }

  torqueC.addSeries(torque)
  powerC.addSeries(power)

  private val plot = new XYPlot()
  plot.setDataset(0, powerC)
  plot.setDataset(1, torqueC)

  {
    val rangeAxis = new NumberAxis("PS")
    rangeAxis.setAutoRangeIncludesZero(true)

    val df = rangeAxis.getLabelFont
    val ndf = new Font(df.getFamily, Font.BOLD, 16)

    rangeAxis.setLabelFont(ndf)

    plot.setRangeAxis(0, rangeAxis)
  }

  {
    val rangeAxis = new NumberAxis("kgf.m")
    rangeAxis.setAutoRangeIncludesZero(true)

    val df = rangeAxis.getLabelFont
    val ndf = new Font(df.getFamily, Font.BOLD, 16)

    rangeAxis.setLabelFont(ndf)

    plot.setRangeAxis(1, rangeAxis)
  }

  {
    val domainAxis = new NumberAxis("RPM")
    domainAxis.setAutoRangeIncludesZero(false)

    val df = domainAxis.getLabelFont
    val ndf = new Font(df.getFamily, Font.BOLD, 16)

    domainAxis.setLabelFont(ndf)

    plot.setDomainAxis(domainAxis)
  }

  plot.mapDatasetToRangeAxis(0, 0)
  plot.mapDatasetToRangeAxis(1, 1)

  private val rendererT = new XYLineAndShapeRenderer()
  private val rendererP = new XYLineAndShapeRenderer()
  rendererT.setSeriesPaint(0, new Color(134, 230, 0))
  rendererT.setSeriesStroke(0, new BasicStroke(2.5f))
  rendererT.setDefaultShapesVisible(false)
  rendererT.setDefaultEntityRadius(8)
  rendererP.setSeriesPaint(0, new Color(230, 134, 0))
  rendererP.setSeriesStroke(0, new BasicStroke(2.5f))
  rendererP.setDefaultShapesVisible(false)
  rendererP.setDefaultEntityRadius(8)

  // Crosshairs maybe better?
  val crosshairStroke = new BasicStroke(
    2.5f,
    BasicStroke.CAP_ROUND,
    BasicStroke.JOIN_ROUND,
    1.0f,
    Array(5f),
    0f,
  )
  val rpmCrosshair = new Crosshair(Double.NaN, Color.WHITE, crosshairStroke)
  val peakTCrosshair = new Crosshair(Double.NaN, Color.WHITE, crosshairStroke)
  val peakPCrosshair = new Crosshair(Double.NaN, Color.WHITE, crosshairStroke)
  val rlCrosshair = new Crosshair(Double.NaN, Color.RED, crosshairStroke)
  val limitCrosshair = new Crosshair(Double.NaN, Color.RED, new BasicStroke(2.5f))

  if (engine.revLimit <= engine.redLine) {
    limitCrosshair.setLabelBackgroundPaint(new Color(255, 143, 143))
    limitCrosshair.setLabelFont(limitCrosshair.getLabelFont.deriveFont(16f).deriveFont(Font.BOLD))
    limitCrosshair.setLabelPaint(Color.BLACK)
    limitCrosshair.setLabelVisible(true)

    limitCrosshair.setValue(engine.revLimit)

    limitCrosshair.setLabelGenerator { _ =>
      s" Limiter @ ${engine.revLimit} RPM "
    }
  }

  Seq(rpmCrosshair, peakTCrosshair, peakPCrosshair, rlCrosshair).foreach { crosshair =>
    crosshair.setLabelBackgroundPaint(Color.WHITE)
    crosshair.setLabelFont(crosshair.getLabelFont.deriveFont(16f).deriveFont(Font.BOLD))
    crosshair.setLabelPaint(Color.BLACK)
    crosshair.setLabelVisible(true)
  }
  rlCrosshair.setLabelBackgroundPaint(new Color(255, 175, 175))

  if (engine.revLimit <= engine.redLine) {
    rlCrosshair.setLabelYOffset(25.5)
    peakPCrosshair.setLabelYOffset(48)
    peakTCrosshair.setLabelYOffset(70.5)
    rpmCrosshair.setLabelYOffset(93)
  } else {
    peakPCrosshair.setLabelYOffset(25.5)
    peakTCrosshair.setLabelYOffset(48)
    rpmCrosshair.setLabelYOffset(70.5)
  }

  rpmCrosshair.setLabelGenerator((crosshair: Crosshair) => {
    val Some(torqueAt) = (0 until torque.getItemCount)
      .map(torque.getDataItem)
      .find(item => item.getX.intValue() == crosshair.getValue.toInt)
      .map(v => BigDecimal(v.getY.doubleValue()).setScale(2, BigDecimal.RoundingMode.HALF_UP))
    val Some(powerAt) = (0 until power.getItemCount)
      .map(power.getDataItem)
      .find(item => item.getX.intValue() == crosshair.getValue.toInt)
      .map(v => BigDecimal(v.getY.doubleValue()).setScale(2, BigDecimal.RoundingMode.HALF_UP))

    s" $torqueAt kgf.m & $powerAt PS @ ${crosshair.getValue.toInt} RPM "
  })

  peakTCrosshair.setLabelGenerator { _ =>
    val (rpm, t) = rawGraphData.peakTorque
    s" ${t.setScale(2, BigDecimal.RoundingMode.HALF_UP)} kgf.m @ $rpm RPM "
  }

  peakTCrosshair.setValue(rawGraphData.peakTorque._1)

  peakPCrosshair.setLabelGenerator { _ =>
    val (rpm, p) = rawGraphData.peakPower
    s" ${p.setScale(2, BigDecimal.RoundingMode.HALF_UP)} PS @ $rpm RPM "
  }

  peakPCrosshair.setValue(rawGraphData.peakPower._1)

  rlCrosshair.setLabelGenerator { _ =>
    s" Redline @ ${engine.redLine} RPM "
  }

  rlCrosshair.setValue(engine.redLine)

  val crosshairs = new CrosshairOverlay
  crosshairs.addDomainCrosshair(peakPCrosshair)
  crosshairs.addDomainCrosshair(peakTCrosshair)
  crosshairs.addDomainCrosshair(limitCrosshair)
  crosshairs.addDomainCrosshair(rlCrosshair)
  crosshairs.addDomainCrosshair(rpmCrosshair)

  plot.setRenderer(0, rendererP)
  plot.setRenderer(1, rendererT)
  plot.setBackgroundPaint(Color.BLACK)

  private val chart = new JFreeChart(carName.name, getFont, plot, true)
  chart.setBackgroundPaint(Color.LIGHT_GRAY)

  private val chartPanel = new ChartPanel(chart)
  chartPanel.setPreferredSize(new Dimension(1280, 800))
  chartPanel.setInitialDelay(0)
  chartPanel.addOverlay(crosshairs)
  chartPanel.addChartMouseListener(this)

  setContentPane(chartPanel)

  // Don't need this.
  override def chartMouseClicked(event: ChartMouseEvent): Unit =
    rpmCrosshair.setVisible(!rpmCrosshair.isVisible)

  override def chartMouseMoved(event: ChartMouseEvent): Unit = {
    val entity = event.getEntity
    entity match {
      case xy: XYItemEntity =>
        val ix = xy.getItem
        val xR = torque.getX(ix).intValue()
        rpmCrosshair.setValue(xR)
      case _                => ()
    }
  }
}
