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

package imperial.measures

import imperial.wrappers.codahale.CodaHaleBackedGauge
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.Matchers._
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class GaugeSpec extends FlatSpec with OneInstancePerTest {
  val metric = mock[com.codahale.metrics.Gauge[Int]]
  val gauge = new CodaHaleBackedGauge(metric)

  "A gauge" should "invoke the underlying function for sugar factory" in {
    val sugared = Gauge({ 1 })
    sugared.value should equal (1)
  }
    
  it should "invoke getValue on the underlying gauge" in {
    when(metric.getValue).thenReturn(1)
    gauge.value should equal (1)
  }
}
