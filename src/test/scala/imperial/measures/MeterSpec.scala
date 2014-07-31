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

import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.scalatest.Matchers._
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class MeterSpec extends FlatSpec with OneInstancePerTest {
  val metric = mock[com.codahale.metrics.Meter]
  val meter = Meter(metric)

  "A meter" should "mark the underlying metric" in {
    meter.mark()
    verify(metric).mark()
  }

  it should "mark the underlying metric by an arbitrary amount" in {
    meter.mark(12)
    verify(metric).mark(12)
  }

  it should "increment the meter on exception when exceptionMeter is used" in {
    a [RuntimeException] should be thrownBy { meter.exceptionMarker( throw new RuntimeException() ) }
    verify(metric).mark()
  }

  it should "increment time execution of partial function" in {
    val pf: PartialFunction[String,String] = { case "test" => throw new RuntimeException() }
    val wrapped = meter.exceptionMarkerPF(pf)
    a [RuntimeException] should be thrownBy { wrapped("test") }
    verify(metric).mark()
    wrapped.isDefinedAt("x") should be (false)
  }
}
