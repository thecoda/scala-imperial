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

import java.util.concurrent.TimeUnit

import com.codahale.{metrics => ch}

object Timer {
  def apply(raw: ch.Timer): Timer = new TimerWrapper(raw)
}

/**
 * A Scala façade class for Timer.
 *
 * Example usage:
 * {{{
 *   class Example(val db: Db) extends Instrumented {
 *     private[this] val loadTimer = timer("load")
 *
 *     def load(id: Long) = loadTimer.time {
 *       db.load(id)
 *     }
 *   }
 * }}}
 */
trait Timer {

  /** Runs f, recording its duration, and returns its result. */
  def time[A](f: => A): A

  /**
   * Converts partial function `pf` into a side-effecting partial function that times
   * every invocation of `pf` for which it is defined. The result is passed unchanged.
   *
   * Example usage:
   * {{{
   *  class Example extends Instrumented {
   *    val isEven: PartialFunction[Int, String] = {
   *      case x if x % 2 == 0 => x+" is even"
   *    }
   *
   *    val isEvenTimer = metrics.timer("isEven")
   *    val timedIsEven: PartialFunction[Int, String] = isEvenTimer.timePF(isEven)
   *
   *    val sample = 1 to 10
   *    sample collect timedIsEven   // timer does 5 measurements
   *  }
   * }}}
   */
   def timePF[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B]

  /** Adds a recorded duration. */
  def update(duration: Long, unit: TimeUnit): Unit

  /** A timing [[com.codahale.metrics.Timer.Context]], which measures an elapsed time in nanoseconds. */
  def timerContext(): TimerContext

  /** The number of durations recorded. */
  def count: Long

  /** A snapshot of the values in the timer's sample. */
  def snapshot: Snapshot

  /** The longest recorded duration in nanoseconds. */
  def max: Long

  /** The shortest recorded duration in nanoseconds. */
  def min: Long

  /** The arithmetic mean of all recorded durations in nanoseconds. */
  def mean: Double

  /** The standard deviation of all recorded durations. */
  def stdDev: Double

  /** The fifteen-minute rate of timings. */
  def fifteenMinuteRate: Double

  /** The five-minute rate of timings. */
  def fiveMinuteRate: Double

  /** The mean rate of timings. */
  def meanRate: Double

  /** The one-minute rate of timings. */
  def oneMinuteRate: Double
}


class TimerWrapper(val raw: ch.Timer) extends Timer{

  def time[A](f: => A): A = {
    val ctx = raw.time
    try {
      f
    } finally {
      ctx.stop
    }
  }

  def timePF[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B] =
    new PartialFunction[A,B] {
      def apply(a: A): B = {
        val ctx = timerContext()
        try {
          pf.apply(a)
        } finally {
          ctx.stop()
        }
      }

      def isDefinedAt(a: A) = pf.isDefinedAt(a)
    }

  def update(duration: Long, unit: TimeUnit): Unit = raw.update(duration, unit)

  def timerContext(): TimerContext = raw.time()

  def count: Long = raw.getCount

  def snapshot: Snapshot = raw.getSnapshot
  def max: Long = snapshot.getMax
  def min: Long = snapshot.getMin
  def mean: Double = snapshot.getMean
  def stdDev: Double = snapshot.getStdDev

  def fifteenMinuteRate: Double = raw.getFifteenMinuteRate
  def fiveMinuteRate: Double = raw.getFiveMinuteRate
  def meanRate: Double = raw.getMeanRate
  def oneMinuteRate: Double = raw.getOneMinuteRate
}
