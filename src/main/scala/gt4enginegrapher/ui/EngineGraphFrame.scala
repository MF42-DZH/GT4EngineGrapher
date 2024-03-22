package gt4enginegrapher.ui

import java.awt.{BasicStroke, Color, Dimension, Font}

import gt4enginegrapher.schema.{Name, SimpleEngine}
import gt4enginegrapher.wrappers.EngineGraph
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.labels.{StandardXYToolTipGenerator, XYToolTipGenerator}
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.ui.ApplicationFrame
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}

case class EngineGraphFrame(
  private val carName: Name,
  private val engine: SimpleEngine,
) extends ApplicationFrame(s"GT4 Engine Graph: ${carName.name}") {
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
  plot.setDataset(0, torqueC)
  plot.setDataset(1, powerC)

  {
    val rangeAxis = new NumberAxis("kgf.m")
    rangeAxis.setAutoRangeIncludesZero(true)

    val df = rangeAxis.getLabelFont
    val ndf = new Font(df.getFamily, Font.BOLD, 16)

    rangeAxis.setLabelFont(ndf)

    plot.setRangeAxis(0, rangeAxis)
  }

  {
    val rangeAxis = new NumberAxis("PS")
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
  rendererT.setDefaultEntityRadius(16)
  rendererP.setSeriesPaint(0, new Color(230, 134, 0))
  rendererP.setSeriesStroke(0, new BasicStroke(2.5f))
  rendererP.setDefaultShapesVisible(false)
  rendererP.setDefaultEntityRadius(16)

  private val tooltips = new XYToolTipGenerator {
    override def generateToolTip(dataset: XYDataset, series: Int, item: Int): String = {
      val x = dataset.getX(series, item)
      val y = dataset.getY(series, item)

      s"${BigDecimal(y.doubleValue()).setScale(2, BigDecimal.RoundingMode.HALF_UP)} ${dataset
          .getSeriesKey(series)} @ ${x.intValue()} RPM"
    }
  }

  rendererT.setSeriesToolTipGenerator(0, tooltips)
  rendererP.setSeriesToolTipGenerator(0, tooltips)

  plot.setRenderer(0, rendererT)
  plot.setRenderer(1, rendererP)
  plot.setBackgroundPaint(Color.BLACK)

  private val chart = new JFreeChart(carName.name, getFont, plot, true)
  chart.setBackgroundPaint(Color.LIGHT_GRAY)

  private val chartPanel = new ChartPanel(chart)
  chartPanel.setPreferredSize(new Dimension(1280, 800))
  chartPanel.setInitialDelay(0)
  setContentPane(chartPanel)
}
