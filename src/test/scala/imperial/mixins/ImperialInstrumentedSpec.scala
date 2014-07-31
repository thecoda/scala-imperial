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

package imperial.mixins

import com.codahale.metrics.MetricRegistry
import imperial.wrappers.codahale.CodaHaleBackedArmoury
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import imperial.measures.Counter
import imperial.Armoury

@RunWith(classOf[JUnitRunner])
class ImperialInstrumentedSpec extends FlatSpec with OneInstancePerTest {

  "An ImperialInstrumented" should "use the owner class as metric base name" in {
    val metricOwner = new MetricOwner
    metricOwner.createCounter()
    verify(metricOwner.metricRegistry).counter("imperial.mixins.ImperialInstrumentedSpec.MetricOwner.cnt")
  }

  private class MetricOwner() extends Instrumented {
    val metricRegistry: MetricRegistry = mock[MetricRegistry]
    val armoury: Armoury = new CodaHaleBackedArmoury(metricRegistry, null) prefixedWith getClass

    def createCounter(): Counter = armoury.counter("cnt")
  }

}
