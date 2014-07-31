package imperial
package wrappers.codahale

import com.codahale.{metrics => ch}
import ch.health.{HealthCheck, HealthCheckRegistry}
import imperial.measures._

class CodaHaleBackedArmoury(
  val metricRegistry: ch.MetricRegistry = new ch.MetricRegistry,
  val healthcheckRegistry: HealthCheckRegistry = new HealthCheckRegistry
) extends RootArmoury {

  def gauge[A](name: String)(f: => A): Gauge[A] =
    new CodaHaleBackedGauge[A](metricRegistry.register(name, new ch.Gauge[A] { def getValue: A = f }))

  def counter(name: String): Counter = new CodaHaleBackedCounter(metricRegistry.counter(name))
  def histogram(name: String): Histogram = new CodaHaleBackedHistogram(metricRegistry.histogram(name))
  def meter(name: String): Meter = new CodaHaleBackedMeter(metricRegistry.meter(name))
  def timer(name: String): Timer = new CodaHaleBackedTimer(metricRegistry.timer(name))

  def healthCheck(name: String, unhealthyMessage: String = "Health check failed")
                 (checker: => HealthCheckMagnet): HealthCheck =
  {
    val check = checker(unhealthyMessage)
    healthcheckRegistry.register(name, check)
    check
  }
}
