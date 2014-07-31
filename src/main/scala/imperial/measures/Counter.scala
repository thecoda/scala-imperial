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
import imperial.wrappers.codahale.CodaHaleBackedCounter

object Counter {
  class CountingPf[A,B](counter: Counter, pf: PartialFunction[A,B]) extends PartialFunction[A,B] {
    def apply(a: A): B = {
      counter.inc(1)
      pf.apply(a)
    }

    def isDefinedAt(a: A) = pf.isDefinedAt(a)
  }

  def apply(raw: ch.Counter): Counter = new CodaHaleBackedCounter(raw)
}


trait Counter {

  /** Wraps partial function pf, incrementing counter once for every execution */
  def count[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B]

  /** Increments the counter by delta. */
  def +=(delta: Long): Unit

  /** Decrements the counter by delta. */
  def -=(delta: Long): Unit

  /** Increments the counter by 1. */
  def inc(delta: Long = 1): Unit

  /** Decrements the counter by 1. */
  def dec(delta: Long = 1): Unit

  /** The current count. */
  def count: Long
}


