package imperial
package wrappers.codahale

import com.codahale.{metrics => ch}
import ch.health.HealthCheckRegistry
import imperial.measures._
import imperial.health.HealthCheckable
import imperial.health.HealthCheck

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

  def healthCheck[T]
    (name: String, unhealthyMessage: String = "Health check failed")
    (payload: => T)
    (implicit checkable: HealthCheckable[T])
  : HealthCheck = {
    val check = new HealthCheck {
      val check = checkable.mkCheck(unhealthyMessage, payload)
    }
    healthcheckRegistry.register(name, wrapHealthCheck(check))
    check
  }

  private[this] def wrapHealthCheck(native: HealthCheck): ch.health.HealthCheck = {
    import ch.health.{HealthCheck => HC}
    import imperial.health.HealthCheck.Result

    new HC {
      override def check(): HC.Result = {
        val r = native.check()
        if(r == Result.healthy)                    HC.Result.healthy()
        else if (r.isHealthy && r.message.isEmpty) HC.Result.healthy()
        else if (r.isHealthy)                      HC.Result.healthy(r.message)
        else                                       HC.Result.unhealthy(r.message)
      }
    }
  }
}
