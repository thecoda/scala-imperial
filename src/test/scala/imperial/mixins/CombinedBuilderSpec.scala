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
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.scalatest.{FunSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import imperial.metrics.Counter
import imperial.MetricName

@RunWith(classOf[JUnitRunner])
class CombinedBuilderSpec extends FunSpec with OneInstancePerTest {

  describe("InstrumentedBuilder combined with CheckedBuilder") {
    it("uses owner class as metric base name") {
      val combinedBuilder = new CombinedBuilder

      combinedBuilder.createCounter()
      verify(combinedBuilder.metricRegistry).counter("imperial.mixins.CombinedBuilderSpec.CombinedBuilder.cnt")

      val check = combinedBuilder.createBooleanHealthCheck { true }
      verify(combinedBuilder.registry).register("imperial.mixins.CombinedBuilderSpec.CombinedBuilder.test", check)
    }

    it("supports overriding the metric base name") {
      val combinedBuilder = new CombinedBuilder {
        override lazy val metricBaseName: MetricName = MetricName("OverriddenBaseName")
      }

      combinedBuilder.createCounter()
      verify(combinedBuilder.metricRegistry).counter("OverriddenBaseName.cnt")

      val check = combinedBuilder.createBooleanHealthCheck { true }
      verify(combinedBuilder.registry).register("OverriddenBaseName.test", check)
    }
  }

  private class CombinedBuilder() extends ImperialInstrumented with ImperialHealthChecked {
    val metricRegistry: MetricRegistry = mock[MetricRegistry]
    val registry: HealthCheckRegistry = mock[HealthCheckRegistry]

    def createCounter(): Counter = metrics.counter("cnt")

    def createBooleanHealthCheck(checker: Boolean): HealthCheck =
      healthCheck("test", "FAIL") { checker }
  }

}