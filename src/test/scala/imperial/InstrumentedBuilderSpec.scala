/*
 * Copyright (c) 2014 Erik van Oosten
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

package imperial

import com.codahale.metrics.MetricRegistry
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.scalatest.{FunSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class InstrumentedBuilderSpec extends FunSpec with OneInstancePerTest {

  describe("InstrumentedBuilder") {
    it("uses owner class as metric base name") {
      val metricOwner = new MetricOwner
      metricOwner.createCounter()
      verify(metricOwner.metricRegistry).counter("imperial.InstrumentedBuilderSpec.MetricOwner.cnt")
    }

    it("supports overriding the metric base name") {
      val metricOwner = new MetricOwner {
        override lazy val metricBaseName: MetricName = MetricName("OverriddenBaseName")
      }
      metricOwner.createCounter()
      verify(metricOwner.metricRegistry).counter("OverriddenBaseName.cnt")
    }
  }

  private class MetricOwner() extends InstrumentedBuilder {
    val metricRegistry: MetricRegistry = mock[MetricRegistry]

    def createCounter(): Counter = metrics.counter("cnt")
  }

}
