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
package mixins

/**
 * The mixin trait for creating a class which is instrumented with metrics.
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
 * class Example(db: Database) extends Instrumented {
 *   private[this] val loading = metrics.timer("loading")
 *
 *   def loadStuff(): Seq[Row] = loading.time {
 *     db.fetchRows()
 *   }
 * }
 * }}}
 *
 * It is also possible to override the metric base name. For example:
 * {{{
 * class Example(db: Database) extends Instrumented {
 *   override lazy val metricBaseName = MetricName("Overridden.Base.Name")
 *   private[this] val loading = metrics.timer("loading")
 *
 *   def loadStuff(): Seq[Row] = loading.time {
 *     db.fetchRows()
 *   }
 * }
 * }}}
 */
trait ImperialInstrumented {

  /** The Armoury where created measures are registered. */
  def armoury: RootArmoury

//  lazy val qualifiedClassBaseName: QualifiedName = QualifiedName(getClass)
//  private[this] lazy val classPathedMetricBuilder = imperial prefixedWith qualifiedClassBaseName

  /** The MetricBuilder that can be used for creating timers, counters, etc. */
  def metrics: Armoury = classPathedMetricBuilder

}
