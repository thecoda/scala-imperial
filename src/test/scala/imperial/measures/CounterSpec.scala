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
import org.mockito.Mockito.{verify, when}
import org.scalatest.Matchers._
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class CounterSpec extends FlatSpec with OneInstancePerTest {
  val metric = mock[com.codahale.metrics.Counter]
  val counter = Counter(metric)

  "A counter" should "increment the underlying metric by an arbitrary amount" in {
    counter += 12
    verify(metric).inc(12)
  }

  it should "decrement the underlying metric by an arbitrary amount" in {
    counter -= 12
    verify(metric).dec(12)
  }

  it should "consult the underlying counter for current count" in {
    when(metric.getCount).thenReturn(1L)
    counter.count should equal (1)
    verify(metric).getCount
  }

  it should "increment counter upon execution of partial function" in {
    val pf: PartialFunction[String, String] = { case "test" => "test" }
    val wrapped = counter.count(pf)
    wrapped("test") should equal ("test")
    verify(metric).inc(1)
    wrapped.isDefinedAt("x") should be (false)
  }
}
