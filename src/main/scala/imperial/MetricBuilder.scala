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

/** Builds and registering metrics. */
trait MetricBuilder {

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
}

object MetricBuilder {
  def apply(baseName: MetricName, registry: ch.MetricRegistry): MetricBuilder =
    new WrappedMetricBuilder(baseName, registry)
}

class WrappedMetricBuilder(val baseName: MetricName, val registry: ch.MetricRegistry) extends MetricBuilder {

  private[this] def metricName(name: String): String = baseName.append(name).name

  def gauge[A](name: String)(f: => A): Gauge[A] =
    new GaugeWrapper[A](registry.register(metricName(name), new ch.Gauge[A] { def getValue: A = f }))

  def counter(name: String): Counter =
    new CounterWrapper(registry.counter(metricName(name)))

  def histogram(name: String): Histogram =
    new HistogramWrapper(registry.histogram(metricName(name)))

  def meter(name: String): Meter =
    new MeterWrapper(registry.meter(metricName(name)))

  def timer(name: String): Timer =
    new TimerWrapper(registry.timer(metricName(name)))

}
