package gtenginegrapher.ui

import java.awt.{BasicStroke, Color, Dimension, Font}

import javax.swing.{JDialog, JFrame}

import gtenginegrapher.schema.{SimpleEngine, SimpleName}
import gtenginegrapher.utils._
import gtenginegrapher.wrappers.{EngineBuilder, EngineGraph}
import org.jfree.chart.{ChartMouseEvent, ChartMouseListener, ChartPanel, JFreeChart}
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.entity.XYItemEntity
import org.jfree.chart.panel.CrosshairOverlay
import org.jfree.chart.plot.{Crosshair, XYPlot}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}

case class EngineGraphPanel(
  owner: JFrame,
  private val name: SimpleName,
  private val engineBuilder: EngineBuilder,
  private val units: (TorqueUnits.KeyVal, PowerUnits.KeyVal),
  private val normalizeGraphs: Boolean,
) extends JDialog(owner, s"Chart for ${name.name}")
  with ChartMouseListener
  with CommonMathOps {
  private val engine: SimpleEngine = engineBuilder.buildEngine()._2
  private val stockEngine: SimpleEngine = engineBuilder.buildStockEngine._2

  private val rawGraphData: EngineGraph = EngineGraph(engine)
  private val rawStockGraphData: EngineGraph = EngineGraph(stockEngine)

  private val torqueC = new XYSeriesCollection
  private val torqueSC = new XYSeriesCollection
  private val torque = new XYSeries(s"Torque (${units._1})")
  private val stockTorque = new XYSeries(s"Stock Torque (${units._1})")

  private val powerC = new XYSeriesCollection
  private val powerSC = new XYSeriesCollection
  private val power = new XYSeries(s"Power (${units._2})")
  private val stockPower = new XYSeries(s"Stock Power (${units._2})")

  println(units._1.multiplier)
  println(units._2.multiplier)

  rawGraphData.points.foreach { case (rpm, (tor, pow)) =>
    torque.add(rpm, tor * units._1.multiplier)
    power.add(rpm, pow * units._2.multiplier)
  }

  rawStockGraphData.points.foreach { case (rpm, (tor, pow)) =>
    stockTorque.add(rpm, tor * units._1.multiplier)
    stockPower.add(rpm, pow * units._2.multiplier)
  }

  torqueC.addSeries(torque)
  powerC.addSeries(power)

  torqueSC.addSeries(stockTorque)
  powerSC.addSeries(stockPower)

  private val plot = new XYPlot()
  plot.setDataset(0, torqueC)
  plot.setDataset(1, powerC)
  plot.setRangeGridlinesVisible(false)

  if (engine != stockEngine) {
    plot.setDataset(2, torqueSC)
    plot.setDataset(3, powerSC)
  }

  {
    val rangeAxis = new NumberAxis(s"${units._1}")
    rangeAxis.setAutoRangeIncludesZero(true)

    if (!normalizeGraphs) {
      rangeAxis.setRange(
        rangeAxis.getRange.getLowerBound,
        math.max(
          torque.getMaxY,
          power.getMaxY,
        ) * 1.04,
      )
    }

    val df = rangeAxis.getLabelFont
    val ndf = new Font(df.getFamily, Font.BOLD, 16)

    rangeAxis.setLabelFont(ndf)

    plot.setRangeAxis(0, rangeAxis)
  }

  {
    val rangeAxis = new NumberAxis(s"${units._2}")
    rangeAxis.setAutoRangeIncludesZero(true)

    if (!normalizeGraphs) {
      rangeAxis.setRange(
        rangeAxis.getRange.getLowerBound,
        math.max(
          torque.getMaxY,
          power.getMaxY,
        ) * 1.04,
      )
    }

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

  if (engine != stockEngine) {
    plot.mapDatasetToRangeAxis(2, 0)
    plot.mapDatasetToRangeAxis(3, 1)
  }

  private val plainStroke = new BasicStroke(2.5f)

  private val rendererT = new XYLineAndShapeRenderer()
  private val rendererP = new XYLineAndShapeRenderer()
  rendererT.setSeriesPaint(0, new Color(134, 230, 0))
  rendererT.setSeriesStroke(0, plainStroke)
  rendererT.setDefaultShapesVisible(false)
  rendererT.setDefaultEntityRadius(8)
  rendererT.setDrawSeriesLineAsPath(true)
  rendererP.setSeriesPaint(0, new Color(230, 134, 0))
  rendererP.setSeriesStroke(0, plainStroke)
  rendererP.setDefaultShapesVisible(false)
  rendererP.setDefaultEntityRadius(8)
  rendererP.setDrawSeriesLineAsPath(true)

  private val dashedStroke =
    new BasicStroke(1.75f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, Array(8f), 0.0f)

  private val rendererST = new XYLineAndShapeRenderer()
  private val rendererSP = new XYLineAndShapeRenderer()
  rendererST.setSeriesPaint(0, new Color(134 / 2, 230 / 2, 0))
  rendererST.setSeriesStroke(0, dashedStroke)
  rendererST.setDefaultShapesVisible(false)
  rendererST.setDefaultEntityRadius(0)
  rendererST.setDrawSeriesLineAsPath(true)
  rendererSP.setSeriesPaint(0, new Color(230 / 2, 134 / 2, 0))
  rendererSP.setSeriesStroke(0, dashedStroke)
  rendererSP.setDefaultShapesVisible(false)
  rendererSP.setDefaultEntityRadius(0)
  rendererSP.setDrawSeriesLineAsPath(true)

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

  if (engine.revLimit < engine.torquePoints.map(_._2).max) {
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

  if (engine.revLimit < engine.torquePoints.map(_._2).max) {
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
      .map(v => BigDecimal(v.getY.doubleValue()).twoDp)
    val Some(powerAt) = (0 until power.getItemCount)
      .map(power.getDataItem)
      .find(item => item.getX.intValue() == crosshair.getValue.toInt)
      .map(v => BigDecimal(v.getY.doubleValue()).twoDp)

    s" $torqueAt ${units._1} & $powerAt ${units._2} @ ${crosshair.getValue.toInt} RPM "
  })

  peakTCrosshair.setLabelGenerator { _ =>
    val (rpm, t) = rawGraphData.peakTorque
    s" ${(t * units._1.multiplier).twoDp} ${units._1} @ $rpm RPM "
  }

  peakTCrosshair.setValue(rawGraphData.peakTorque._1)

  peakPCrosshair.setLabelGenerator { _ =>
    val (rpm, p) = rawGraphData.peakPower
    s" ${(p * units._2.multiplier).twoDp} ${units._2} @ $rpm RPM "
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

  plot.setRenderer(0, rendererT)
  plot.setRenderer(1, rendererP)

  if (engine != stockEngine) {
    plot.setRenderer(2, rendererST)
    plot.setRenderer(3, rendererSP)
  }

  plot.setBackgroundPaint(Color.BLACK)

  private val chart = new JFreeChart(name.name, getFont, plot, true)
  chart.setBackgroundPaint(Color.LIGHT_GRAY)
  chart.getTitle.setFont(chart.getTitle.getFont.deriveFont(24f).deriveFont(Font.BOLD))

  private val chartPanel = new ChartPanel(chart)
  chartPanel.setPreferredSize(new Dimension(1280, 800))
  chartPanel.setInitialDelay(0)
  chartPanel.addOverlay(crosshairs)
  chartPanel.addChartMouseListener(this)

  this.setContentPane(chartPanel)

  override def chartMouseClicked(event: ChartMouseEvent): Unit =
    rpmCrosshair.setVisible(!rpmCrosshair.isVisible)

  override def chartMouseMoved(event: ChartMouseEvent): Unit = {
    val entity = event.getEntity
    entity match {
      case xy: XYItemEntity =>
        if (Seq(torqueC, powerC).contains(xy.getDataset)) {
          val ix = xy.getItem
          val xR = torque.getX(ix).intValue()
          rpmCrosshair.setValue(xR)
        }
      case _                => ()
    }
  }
}
