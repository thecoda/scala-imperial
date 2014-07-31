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

import java.util.concurrent.TimeUnit

import org.junit.runner.RunWith
import org.mockito.Mockito.{verify, when}
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import org.scalatest.{FlatSpec, OneInstancePerTest}

object TimerSpec {
  case class Result()

  def myFunc(x: String): List[Result] = {
    List(Result())
  }

}

@RunWith(classOf[JUnitRunner])
class TimerSpec extends FlatSpec with OneInstancePerTest {

  import TimerSpec._
  val metric = mock[com.codahale.metrics.Timer]
  val timer = Timer(metric)
  val context = mock[com.codahale.metrics.Timer.Context]
  when(metric.time()).thenReturn(context)

  "A timer" should "time a passed closure" in {
    timer.time { 1 }
    verify(metric).time()
    verify(context).stop()
  }

  it should "update the underlying metric" in {
    timer.update(1L,TimeUnit.MILLISECONDS)
    verify(metric).update(1L,TimeUnit.MILLISECONDS)
  }

  it should "increment time execution of partial function" in {
    val pf: PartialFunction[String,String] = { case "test" => "test" }
    val wrapped = timer.timePF(pf)
    wrapped("test") should equal ("test")
    verify(metric).time()
    verify(context).stop()
    wrapped.isDefinedAt("x") should be (false)
  }

  it should "correctly infers the type" in {
    val someString = "someString"
    val timed = timer.time(myFunc(someString))
    timed.isInstanceOf[List[_]] should be (true)
    timed(0).isInstanceOf[Result] should be (true)

    val pf: PartialFunction[String,String] = { case x: String => x }
    val timedPF = timer.timePF( pf )
    timedPF.isInstanceOf[PartialFunction[_,_]] should be (true)
    timedPF("x") should be ("x")
  }
}
