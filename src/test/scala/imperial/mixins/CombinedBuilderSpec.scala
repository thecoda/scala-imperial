/*
 * Copyright (c) 2013-2014 Erik van Oosten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package imperial.mixins

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.{HealthCheck, HealthCheckRegistry}
import imperial.wrappers.codahale.CodaHaleBackedArmoury
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import imperial.measures.Counter
import imperial.Armoury

@RunWith(classOf[JUnitRunner])
class CombinedBuilderSpec extends FlatSpec with OneInstancePerTest {

  "InstrumentedBuilder combined with CheckedBuilder" should "uses owner class as metric base name" in {
    val combinedBuilder = new CombinedBuilder

    combinedBuilder.createCounter()
    verify(combinedBuilder.metricRegistry).counter("imperial.mixins.CombinedBuilderSpec.CombinedBuilder.cnt")

    val check = combinedBuilder.createBooleanHealthCheck { true }
    verify(combinedBuilder.healthCheckRegistry).register("imperial.mixins.CombinedBuilderSpec.CombinedBuilder.test", check)
  }

  private class CombinedBuilder() extends Instrumented {
    val metricRegistry: MetricRegistry = mock[MetricRegistry]
    val healthCheckRegistry: HealthCheckRegistry = mock[HealthCheckRegistry]

    val armoury: Armoury =  new CodaHaleBackedArmoury(metricRegistry, healthCheckRegistry) prefixedWith getClass

    def createCounter(): Counter = armoury.counter("cnt")

    def createBooleanHealthCheck(checker: Boolean): HealthCheck =
      armoury.healthCheck("test", "FAIL") { checker }
  }

}
