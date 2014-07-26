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

package imperial

import akka.actor.{Actor, ActorSystem}
import imperial.Meter.ExceptionMarkerPf
import imperial.mocks.MockMetricBuilder
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{when, verify, never}
import org.scalatest.FunSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

object TestFixture {

//  class Fixture  {
//    val mockCounter = mock[Counter]
//    val mockTimer = mock[Timer]
//    val mockTimerContext = mock[TimerContext]
//    val mockMeter = mock[Meter]
//
//    val pf: PartialFunction[Any,Unit] = {
//      case _ =>
//    }
//
//    when(mockTimer.timerContext()).thenReturn(mockTimerContext)
//    when(mockCounter.count(any[PartialFunction[Any,Unit]])).thenReturn(pf)
//    when(mockTimer.timePF(any[PartialFunction[Any,Unit]])).thenReturn(pf)
//    when(mockMeter.exceptionMarkerPF).thenReturn(new ExceptionMarkerPf(mockMeter))
//  }

  trait MetricRegistryFixture extends InstrumentedBuilder {
//    val fixture: Fixture

    val metricRegistry = null

    var counterName: String = null


    def builder: MetricBuilder
    override def metrics = builder
  }

  class TestActor(val builder: MetricBuilder) extends Actor with MetricRegistryFixture {
    val messages = new scala.collection.mutable.ListBuffer[String]()

    def receive = { case message: String => println("message: " + message); messages += message }
  }

  class ExceptionThrowingTestActor(val builder: MetricBuilder) extends Actor with MetricRegistryFixture {
    def receive = {
      case _ => throw new RuntimeException()
    }
  }


  class CounterTestActor(builder: MetricBuilder) extends TestActor(builder) with ReceiveCounterActor {
    override def receiveCounterName = "receiveCounter"
  }

  class TimerTestActor(builder: MetricBuilder) extends TestActor(builder) with ReceiveTimerActor

  class ExceptionMeterTestActor(builder: MetricBuilder) extends ExceptionThrowingTestActor(builder) with ReceiveExceptionMeterActor

  class ComposedActor(builder: MetricBuilder) extends TestActor(builder)
    with ReceiveCounterActor with ReceiveTimerActor with ReceiveExceptionMeterActor

}

@RunWith(classOf[JUnitRunner])
class ActorMetricsSpec extends FunSpec {
  import akka.testkit.TestActorRef
  import TestFixture._

  implicit val system = ActorSystem()

  describe("A counter actor") {
    it("increments counter on new messages") {
      val builder = new MockMetricBuilder
      val ref = TestActorRef(new CounterTestActor(builder))

      ref.underlyingActor.receive should not be (null)
      ref ! "test"
      assert(builder.counter("receiveCounter").count === 1)
//      verify(fixture.mockCounter).count(any[PartialFunction[Any,Unit]])
//      ref.underlyingActor.counterName should equal ("receiveCounter")
    }
  }

  describe("A timer actor") {
    it("times a message processing") {
      val builder = new MockMetricBuilder
      val ref = TestActorRef(new TimerTestActor(builder))
      ref ! "test"
      println(builder.describe)
//      val receiveTimer = ref.underlyingActor.timer
      val receiveTimer = builder.timer("imperial.TestFixture.TimerTestActor.receiveTimer")
      assert(receiveTimer.count === 1)
    }
  }

  describe("A exception meter actor") {
    it("meters thrown exceptions") {
      val builder = new MockMetricBuilder
      val ref = TestActorRef(new ExceptionMeterTestActor(builder))
      intercept[RuntimeException] { ref.receive("test") }

      val receiveExceptionMeter = ref.underlyingActor.meter
      assert(receiveExceptionMeter.count === 1)
    }
  }

  describe("A composed actor") {
    it("counts and times processing of messages") {
      val builder = new MockMetricBuilder
      val ref = TestActorRef(new ComposedActor(builder))
      ref ! "test"

      val receiveTimer = ref.underlyingActor.timer
      val receiveExceptionMeter = ref.underlyingActor.meter
      val receiveCounter = ref.underlyingActor.counter

      assert(receiveTimer.count === 1)
      assert(receiveExceptionMeter.count === 1)
      assert(receiveCounter.count === 0)

      ref.underlyingActor.counterName should equal ("nl.grons.metrics.scala.TestFixture.ComposedActor.receiveCounter")
    }
  }

}
