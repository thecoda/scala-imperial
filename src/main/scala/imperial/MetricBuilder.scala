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

  /**
   * Registers a new gauge metric.
   *
   * @param name  the name of the gauge
   * @param scope the scope of the gauge or null for no scope
   */
  def gauge[A](name: String, scope: String = null)(f: => A): Gauge[A]

  /**
   * Creates a new counter metric.
   *
   * @param name  the name of the counter
   * @param scope the scope of the counter or null for no scope
   */
  def counter(name: String, scope: String = null): Counter

  /**
   * Creates a new histogram metrics.
   *
   * @param name   the name of the histogram
   * @param scope  the scope of the histogram or null for no scope
   */
  def histogram(name: String, scope: String = null): Histogram

  /**
   * Creates a new meter metric.
   *
   * @param name the name of the meter
   * @param scope the scope of the meter or null for no scope
   */
  def meter(name: String, scope: String = null): Meter

  /**
   * Creates a new timer metric.
   *
   * @param name the name of the timer
   * @param scope the scope of the timer or null for no scope
   */
  def timer(name: String, scope: String = null): Timer
}

object MetricBuilder {
  def apply(baseName: MetricName, registry: ch.MetricRegistry): MetricBuilder =
    new WrappedMetricBuilder(baseName, registry)
}

class WrappedMetricBuilder(val baseName: MetricName, val registry: ch.MetricRegistry) extends MetricBuilder {

  private[this] def metricName(name: String, scope: String = null): String =
    baseName.append(name, scope).name

  def gauge[A](name: String, scope: String = null)(f: => A): Gauge[A] =
    new GaugeWrapper[A](registry.register(metricName(name, scope), new ch.Gauge[A] { def getValue: A = f }))

  def counter(name: String, scope: String = null): Counter =
    new CounterWrapper(registry.counter(metricName(name, scope)))

  def histogram(name: String, scope: String = null): Histogram =
    new HistogramWrapper(registry.histogram(metricName(name, scope)))

  def meter(name: String, scope: String = null): Meter =
    new MeterWrapper(registry.meter(metricName(name, scope)))

  def timer(name: String, scope: String = null): Timer =
    new TimerWrapper(registry.timer(metricName(name, scope)))

}
