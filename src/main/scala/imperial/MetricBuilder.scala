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

/** Builds and registering metrics. */
trait MetricBuilder {

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

  def rootBuilder: MetricBuilder
  def parentBuilder: MetricBuilder
  
  // TODO: make this the API, wrap the ch registry out of sight
  def atBase(nestedBase: MetricName): MetricBuilder = new NestedMetricBuilder(this, baseName + nestedBase)
  def atBase(nestedBase: Class[_]): MetricBuilder = new NestedMetricBuilder(this, baseName + MetricName(nestedBase))
//  def atBase(base: akka.actor.ActorPath): MetricBuilder
  def atBase(nestedBase: String): MetricBuilder = new NestedMetricBuilder(this, baseName.append(nestedBase))
}

trait RootMetricBuilder extends MetricBuilder {
  def rootBuilder: MetricBuilder = this
  def parentBuilder: MetricBuilder = this
  val baseName: MetricName = MetricName("")
}

class NestedMetricBuilder(val parentBuilder: MetricBuilder, val baseName: MetricName) extends MetricBuilder {

  def rootBuilder: MetricBuilder = parentBuilder.rootBuilder

  private[this] def deriveName(name: String): String = baseName.append(name).name

  def gauge[A](name: String)(f: => A): Gauge[A] = parentBuilder.gauge(deriveName(name))(f)
  def counter(name: String): Counter = parentBuilder.counter(deriveName(name))
  def histogram(name: String): Histogram = parentBuilder.histogram(deriveName(name))
  def meter(name: String): Meter = parentBuilder.meter(deriveName(name))
  def timer(name: String): Timer = parentBuilder.timer(deriveName(name))

}

object MetricBuilder {
  def wrap(registry: ch.MetricRegistry): RootMetricBuilder =
    new WrappedMetricBuilder(registry)
}

class WrappedMetricBuilder(val r: ch.MetricRegistry) extends RootMetricBuilder {
  def gauge[A](name: String)(f: => A): Gauge[A] =
    new GaugeWrapper[A](r.register(name, new ch.Gauge[A] { def getValue: A = f }))

  def counter(name: String): Counter = new CounterWrapper(r.counter(name))
  def histogram(name: String): Histogram = new HistogramWrapper(r.histogram(name))
  def meter(name: String): Meter = new MeterWrapper(r.meter(name))
  def timer(name: String): Timer = new TimerWrapper(r.timer(name))

}
