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

import akka.actor.{Actor, ActorSystem}
import imperial.mocks.MockArmoury
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import imperial.Armoury

object ActorMetricsSpec {

  object TestFixture {

    class TestActor(val armoury: Armoury) extends Actor with InstrumentedActor {
      val messages = new scala.collection.mutable.ListBuffer[String]()

      def receive = {
        case message: String => println("message: " + message); messages += message
      }
    }

    class ExceptionThrowingTestActor(val armoury: Armoury) extends Actor with InstrumentedActor {
      def receive = {
        case _ => throw new RuntimeException()
      }
    }


    class CounterTestActor(armoury: Armoury) extends TestActor(armoury) with CountReceives {
      override def receiveCounterName = "receiveCounter"
    }

    class TimerTestActor(armoury: Armoury) extends TestActor(armoury) with TimeReceives

    class ExceptionMeterTestActor(armoury: Armoury)
      extends ExceptionThrowingTestActor(armoury)
      with MeterReceiveExceptions

    class ComposedActor(armoury: Armoury)
      extends TestActor(armoury)
      with CountReceives
      with TimeReceives
      with MeterReceiveExceptions

  }

}

@RunWith(classOf[JUnitRunner])
class ActorMetricsSpec extends FlatSpec {
  import akka.testkit.TestActorRef
  import ActorMetricsSpec.TestFixture._

  implicit val system = ActorSystem()

  "A counter actor" should "increments counter on new messages" in {
    val armoury = new MockArmoury
    val ref = TestActorRef(new CounterTestActor(armoury))

    ref.underlyingActor.receive should not be (null)
    ref ! "test"
    assert(ref.underlyingActor.armoury.counter("receiveCounter").count === 1)
  }

  "A timer actor" should "time a message processing" in {
    val armoury = new MockArmoury
    val ref = TestActorRef(new TimerTestActor(armoury))
    ref ! "test"
    val receiveTimer = ref.underlyingActor.timer
    assert(receiveTimer.count === 1)
  }

  "A exception meter actor" should "meter thrown exceptions" in {
    val armoury = new MockArmoury
    val ref = TestActorRef(new ExceptionMeterTestActor(armoury))
    intercept[RuntimeException] { ref.receive("test") }

    val receiveExceptionMeter = ref.underlyingActor.meter
    assert(receiveExceptionMeter.count === 1)
  }

  "A composed actor" should "count and time processing of messages" in {
    val armoury = new MockArmoury
    val ref = TestActorRef(new ComposedActor(armoury))
    ref ! "test"

    val receiveTimer = ref.underlyingActor.timer
    val receiveExceptionMeter = ref.underlyingActor.meter
    val receiveCounter = ref.underlyingActor.counter

    assert(receiveTimer.count === 1)
    assert(receiveExceptionMeter.count === 0)
    assert(receiveCounter.count === 1)
  }

}
