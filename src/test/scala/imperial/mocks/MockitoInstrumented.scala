package imperial
package mocks

import imperial.mixins.Instrumented
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.HealthCheckRegistry
import imperial.wrappers.codahale.CodaHaleBackedArmoury
import org.scalatest.mock.MockitoSugar._


trait MockitoInstrumented extends Instrumented {
  val metricRegistry: MetricRegistry = mock[MetricRegistry]
  val healthCheckRegistry: HealthCheckRegistry = mock[HealthCheckRegistry]

  val armoury: Armoury = new CodaHaleBackedArmoury(metricRegistry, healthCheckRegistry) prefixedWith getClass
}
