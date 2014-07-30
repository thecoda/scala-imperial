package imperial.mixins

import imperial.MetricName

//TODO: fold healthchecking into ImperialInstrumented, and drop this class
trait ImperialBase {
  lazy val qualifiedClassBaseName = MetricName(getClass)
  
  /** The base name for all metrics and health checks created from this builder. */
  def metricBaseName: MetricName = qualifiedClassBaseName
}
