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

import scala.util.control.ControlThrowable

object Meter {
  class ExceptionMarker(val meter: Meter) {
    def apply[A](f: => A): A = {
      try {
        f
      } catch {
        case e: ControlThrowable =>
          // ControlThrowable is used by Scala for control, it is equivalent to success.
          throw e
        case e: Throwable =>
          meter.mark()
          throw e
      }
    }
  }

  class ExceptionMarkerPf(val meter: Meter) {
    def apply[A, B](pf: PartialFunction[A, B]): PartialFunction[A, B] = new PartialFunction[A, B] {
      def apply(a: A): B = {
        try {
          pf.apply(a)
        } catch {
          case e: Throwable =>
            meter.mark()
            throw e
        }
      }

      def isDefinedAt(a: A) = pf.isDefinedAt(a)
    }
  }

  def apply(raw: ch.Meter): Meter = new MeterWrapper(raw)
}

/**
 * Example usage:
 * {{{
 *   class Example(val db: Db) extends Instrumented {
 *     private[this] val rowsLoadedMeter = meter("rowsLoaded")
 *
 *     def load(id: Long): Seq[Row] = {
 *       val rows = db.load(id)
 *       rowsLoaded.mark(rows.size)
 *       rows
 *     }
 *   }
 * }}}
 */
trait Meter {

  /**
   * Gives a marker that runs f, marks the meter on an exception, and returns result of f.
   *
   * Example usage:
   * {{{
   *   class Example(val db: Db) extends Instrumented {
   *     private[this] val loadExceptionMeter = metrics.meter("load").exceptionMarker
   *
   *     def load(id: Long) = loadExceptionMeter {
   *       db.load(id)
   *     }
   *   }
   * }}}
   */
  def exceptionMarker: Meter.ExceptionMarker = new Meter.ExceptionMarker(this)

  /**
   * Converts partial function `pf` into a side-effecting partial function that meters
   * thrown exceptions for every invocation of `pf` (for the cases it is defined).
   * The result is passed unchanged.
   *
   * Example usage:
   * {{{
   *  class Example extends Instrumented {
   *    val isEven: PartialFunction[Int, String] = {
   *      case x if x % 2 == 0 => x+" is even"
   *      case 5 => throw new IllegalArgumentException("5 is unlucky")
   *    }
   *
   *    val isEvenExceptionMeter = metrics.meter("isEvenExceptions")
   *    val meteredIsEven: PartialFunction[Int, String] = isEvenExceptionMeter.exceptionMarkerPF(isEven)
   *
   *    val sample = 1 to 10
   *    sample collect meteredIsEven   // the meter counts 1 exception
   *  }
   * }}}
   */
  def exceptionMarkerPF: Meter.ExceptionMarkerPf = new Meter.ExceptionMarkerPf(this)

  /** Marks the occurrence of an event. */
  def mark(): Unit

  /** Marks the occurrence of a given number of events. */
  def mark(count: Long): Unit

  /** The number of events which have been marked. */
  def count: Long

  /**
   * The fifteen-minute exponentially-weighted moving average rate at
   * which events have occurred since the meter was created.
   * <p>
   * This rate has the same exponential decay factor as the fifteen-minute load
   * average in the top Unix command.
   */
  def fifteenMinuteRate: Double

  /**
   * The five-minute exponentially-weighted moving average rate at
   * which events have occurred since the meter was created.
   * <p>
   * This rate has the same exponential decay factor as the five-minute load
   * average in the top Unix command.
   */
  def fiveMinuteRate: Double

  /**
   * The mean rate at which events have occurred since the meter was
   * created.
   */
  def meanRate: Double

  /**
   * The one-minute exponentially-weighted moving average rate at
   * which events have occurred since the meter was created.
   * <p>
   * This rate has the same exponential decay factor as the one-minute load
   * average in the top Unix command.
   */
  def oneMinuteRate: Double
}


class MeterWrapper(val raw: ch.Meter) extends Meter {

  def mark(): Unit = raw.mark()
  def mark(count: Long): Unit = raw mark count

  def count: Long = raw.getCount

  def fifteenMinuteRate: Double = raw.getFifteenMinuteRate
  def fiveMinuteRate: Double = raw.getFiveMinuteRate
  def oneMinuteRate: Double = raw.getOneMinuteRate
  def meanRate: Double = raw.getMeanRate
}

