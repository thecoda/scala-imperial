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

package imperial.measures

import com.codahale.{metrics => ch}
import imperial.`package`._
import imperial.wrappers.codahale.CodaHaleBackedHistogram

object Histogram {
  def apply(raw: ch.Histogram): Histogram = new CodaHaleBackedHistogram(raw)
}

trait Histogram {

  /** Adds the recorded value to the histogram sample. */
  def +=(value: Long): Unit

  /** Adds the recorded value to the histogram sample. */
  def +=(value: Int): Unit

  /** The number of values recorded. */
  def count: Long

  /** A snapshot of the values in the histogram's sample. */
  def snapshot: Snapshot

  /** The largest recorded value. */
  def max: Long

  /** The smallest recorded value. */
  def min: Long

  /** The arithmetic mean of all recorded values. */
  def mean: Double

  /** The standard deviation of all recorded values. */
  def stdDev: Double

}



