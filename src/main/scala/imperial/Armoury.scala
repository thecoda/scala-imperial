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

import com.codahale.{metrics => ch}
import imperial.measures._
import com.codahale.metrics.health.HealthCheck

/** Builds and registering metrics. */
trait Armoury {

  def baseName: MetricName

  /** Registers a new gauge metric. */
  def gauge[A](name: String)(f: => A): Gauge[A]

  /** Creates a new counter metric. */
  def counter(name: String): Counter

  /** Creates a new histogram metrics. */
  def histogram(name: String): Histogram

  /** Creates a new meter metric. */
  def meter(name: String): Meter

  /** Creates a new timer metric. */
  def timer(name: String): Timer

  def healthCheck(name: String, unhealthyMessage: String)(checker: => HealthCheckMagnet): HealthCheck

  def rootBuilder: Armoury
  def parentBuilder: Armoury
  
  def prefixedWith(nestedBase: MetricName): Armoury = new NestedArmoury(this, baseName + nestedBase)
  def prefixedWith(nestedBase: Class[_]): Armoury = new NestedArmoury(this, baseName + MetricName(nestedBase))
//  def prefixedWith(base: akka.actor.ActorPath): MetricBuilder
  def prefixedWith(nestedBase: String): Armoury = new NestedArmoury(this, baseName.append(nestedBase))
}

trait RootArmoury extends Armoury {
  def rootBuilder: Armoury = this
  def parentBuilder: Armoury = this
  val baseName: MetricName = MetricName("")
}

class NestedArmoury(val parentBuilder: Armoury, val baseName: MetricName) extends Armoury {

  def rootBuilder: Armoury = parentBuilder.rootBuilder

  private[this] def deriveName(name: String): String = baseName.append(name).name

  def gauge[A](name: String)(f: => A): Gauge[A] = parentBuilder.gauge(deriveName(name))(f)
  def counter(name: String): Counter = parentBuilder.counter(deriveName(name))
  def histogram(name: String): Histogram = parentBuilder.histogram(deriveName(name))
  def meter(name: String): Meter = parentBuilder.meter(deriveName(name))
  def timer(name: String): Timer = parentBuilder.timer(deriveName(name))

  def healthCheck(name: String, unhealthyMessage: String = "Health check failed")
                 (checker: => HealthCheckMagnet): HealthCheck
                 = parentBuilder.healthCheck(deriveName(name), unhealthyMessage)(checker)


}

object Armoury {
  def wrap(metricRegistry: ch.MetricRegistry, healthcheckRegistry: ch.health.HealthCheckRegistry): RootArmoury =
    new WrappedArmoury(metricRegistry, healthcheckRegistry)
}

class WrappedArmoury(
  val metricRegistry: ch.MetricRegistry,
  val healthcheckRegistry: ch.health.HealthCheckRegistry
) extends RootArmoury {

  def gauge[A](name: String)(f: => A): Gauge[A] =
    new GaugeWrapper[A](metricRegistry.register(name, new ch.Gauge[A] { def getValue: A = f }))

  def counter(name: String): Counter = new CounterWrapper(metricRegistry.counter(name))
  def histogram(name: String): Histogram = new HistogramWrapper(metricRegistry.histogram(name))
  def meter(name: String): Meter = new MeterWrapper(metricRegistry.meter(name))
  def timer(name: String): Timer = new TimerWrapper(metricRegistry.timer(name))

  def healthCheck(name: String, unhealthyMessage: String = "Health check failed")
                 (checker: => HealthCheckMagnet): HealthCheck =
  {
    val check = checker(unhealthyMessage)
    healthcheckRegistry.register(name, check)
    check
  }
}
