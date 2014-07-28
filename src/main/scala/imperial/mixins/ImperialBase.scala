package imperial.mixins

import imperial.MetricName

trait ImperialBase {
  lazy val classBasedBaseName = MetricName(getClass)
  /** The base name for all metrics created from this builder. */
  def metricBaseName: MetricName = classBasedBaseName
}
