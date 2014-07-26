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
import imperial.mocks.MockMetricBuilder
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

object TestFixture {

  trait MetricRegistryFixture extends InstrumentedBuilder {
    val metricRegistry = null
    def builder: MetricBuilder
    override def metrics: MetricBuilder = builder
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
class ActorMetricsSpec extends FlatSpec {
  import akka.testkit.TestActorRef
  import TestFixture._

  implicit val system = ActorSystem()

  "A counter actor" should "increments counter on new messages" in {
    val builder = new MockMetricBuilder
    val ref = TestActorRef(new CounterTestActor(builder))

    ref.underlyingActor.receive should not be (null)
    ref ! "test"
    assert(builder.counter("receiveCounter").count === 1)
  }

  "A timer actor" should "time a message processing" in {
    val builder = new MockMetricBuilder
    val ref = TestActorRef(new TimerTestActor(builder))
    ref ! "test"
    println(builder.describe)
//      val receiveTimer = ref.underlyingActor.timer
    val receiveTimer = builder.timer("imperial.TestFixture.TimerTestActor.receiveTimer")
    assert(receiveTimer.count === 1)
  }

  "A exception meter actor" should "meter thrown exceptions" in {
    val builder = new MockMetricBuilder
    val ref = TestActorRef(new ExceptionMeterTestActor(builder))
    intercept[RuntimeException] { ref.receive("test") }

    val receiveExceptionMeter = ref.underlyingActor.meter
    assert(receiveExceptionMeter.count === 1)
  }

  "A composed actor" should "count and time processing of messages" in {
    val builder = new MockMetricBuilder
    val ref = TestActorRef(new ComposedActor(builder))
    ref ! "test"

    val receiveTimer = ref.underlyingActor.timer
    val receiveExceptionMeter = ref.underlyingActor.meter
    val receiveCounter = ref.underlyingActor.counter

    assert(receiveTimer.count === 1)
    assert(receiveExceptionMeter.count === 0)
    assert(receiveCounter.count === 1)
  }

}
