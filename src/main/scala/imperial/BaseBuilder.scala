package imperial

trait BaseBuilder {
  /** The base name for all metrics created from this builder. */
  lazy val metricBaseName = MetricName(getClass)
}
