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

import imperial.mocks.MockArmoury
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.concurrent.{Await, ExecutionContext, Promise}
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class FutureMetricsSpec extends FlatSpec {

  implicit def sameThreadEc: ExecutionContext = new ExecutionContext {
    def execute(runnable: Runnable): Unit = runnable.run
    def reportFailure(t: Throwable): Unit = throw t
  }

  trait WithMockMetrics extends FutureMetrics with Instrumented {
    val armoury = new MockArmoury
  }

  "A future timer" should "time an execution" in new WithMockMetrics {
    val f = timed("test") {
      Thread.sleep(10L)
      10
    }
    val result = Await.result(f, 300.millis)
    assert(armoury.timer("test").count === 1)
    result should be (10)
  }

  it should "attach an onComplete listener" in new WithMockMetrics {
    val p = Promise[String]()
    val f = timing("test") {
      p.future
    }
    p.success("test")
    val result = Await.result(f, 50.millis)
    result should be ("test")
    assert(armoury.timer("test").count === 1)
//      verify(mockTimerContext).stop()
  }


}
