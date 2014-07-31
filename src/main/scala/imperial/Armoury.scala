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

import imperial.measures._
import com.codahale.metrics.health.HealthCheck

/** Builds and registering metrics. */
trait Armoury {

  def baseName: QualifiedName

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
  
  def prefixedWith(nestedBase: QualifiedName): Armoury = new NestedArmoury(this, baseName + nestedBase)
  def prefixedWith(nestedBase: Class[_]): Armoury = new NestedArmoury(this, baseName + QualifiedName(nestedBase))
//  def prefixedWith(base: akka.actor.ActorPath): MetricBuilder
  def prefixedWith(nestedBase: String): Armoury = new NestedArmoury(this, baseName.append(nestedBase))
}

trait RootArmoury extends Armoury {
  def rootBuilder: Armoury = this
  def parentBuilder: Armoury = this
  val baseName: QualifiedName = QualifiedName("")
}

class NestedArmoury(val parentBuilder: Armoury, val baseName: QualifiedName) extends Armoury {

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



