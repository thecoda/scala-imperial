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

package akka.imperial

import akka.actor.Actor
import imperial._

/**
 * Stackable actor trait which counts received messages.
 *
 * Metric name defaults to the class of the actor (e.g. `ExampleActor` below) + .`receiveCounter`
 *
 * Use it as follows:
 * {{{
 * object Application {
 *   // The application wide metrics registry.
 *   val metricRegistry = new com.codahale.metrics.MetricRegistry()
 * }
 * trait Instrumented extends InstrumentedBuilder {
 *   val metricRegistry = Application.metricRegistry
 * }
 *
 * class ExampleActor extends ReceiveCounterActor with Instrumented {
 *
 *   def receive = {
 *     case _ => doWork()
 *   }
 * }
 * }}}
 */
trait ReceiveCounterActor extends Actor { self: InstrumentedBuilder =>

  def receiveCounterName: String = MetricName(getClass).append("receiveCounter").name
  lazy val counter: Counter = metrics.counter(receiveCounterName)

  override protected[akka] def aroundReceive(receive: Actor.Receive, msg: Any): Unit = {
    super.aroundReceive(receive, msg)
    counter.inc()
  }
}

/**
 * Stackable actor trait which times the message receipt.
 *
 * Metric name defaults to the class of the actor (e.g. `ExampleActor` below) + `.receiveTimer`
 *
 * Use it as follows:
 * {{{
 * object Application {
 *   // The application wide metrics registry.
 *   val metricRegistry = new com.codahale.metrics.MetricRegistry()
 * }
 * trait Instrumented extends InstrumentedBuilder {
 *   val metricRegistry = Application.metricRegistry
 * }
 *
 * class ExampleActor extends ReceiveTimerActor with Instrumented {
 *
 *   def receive = {
 *     case _ => doWork()
 *   }
 * }
 * }}}
 */
trait ReceiveTimerActor extends Actor { self: InstrumentedBuilder =>

  def receiveTimerName: String = MetricName(getClass).append("receiveTimer").name
  lazy val timer: Timer = metrics.timer(receiveTimerName)

  override protected[akka] def aroundReceive(receive: Actor.Receive, msg: Any): Unit = timer.time(
    super.aroundReceive(receive, msg)
  )
}

/**
 * Stackable actor trait which meters thrown exceptions.
 *
 * Metric name defaults to the class of the actor (e.g. `ExampleActor` below) + `.receiveExceptionMeter`
 *
 * Use it as follows:
 * {{{
 * object Application {
 *   // The application wide metrics registry.
 *   val metricRegistry = new com.codahale.metrics.MetricRegistry()
 * }
 * trait Instrumented extends InstrumentedBuilder {
 *   val metricRegistry = Application.metricRegistry
 * }
 *
 * class ExampleActor extends ReceiveTimerActor with Instrumented {
 *
 *   def receive = {
 *     case _ => doWork()
 *   }
 * }
 * }}}
 */
trait ReceiveExceptionMeterActor extends Actor { self: InstrumentedBuilder =>

  def receiveExceptionMeterName: String = MetricName(getClass).append("receiveExceptionMeter").name
  lazy val meter: Meter = metrics.meter(receiveExceptionMeterName)

  override protected[akka] def aroundReceive(receive: Actor.Receive, msg: Any): Unit = {
    meter.exceptionMarker(super.aroundReceive(receive, msg))
  }
}
