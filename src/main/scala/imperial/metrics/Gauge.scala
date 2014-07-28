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

package imperial.metrics

import com.codahale.{metrics => ch}

object Gauge {
  def apply[A](f: => A): Gauge[A] = new GaugeWrapper[A](
    new ch.Gauge[A] { def getValue = f }
  )
}

trait Gauge[T] {
  /** The current value. */
  def value: T
}

/** A Scala façade class for Gauge. */
class GaugeWrapper[T](val raw: ch.Gauge[T]) extends Gauge[T] {
  def value: T = raw.getValue
}
