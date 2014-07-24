package imperial
package mocks

import scala.concurrent.duration._

class MockTimer extends Timer {
  def time[A](action: => A): A = ???
  def timerContext: TimerContext = ???
  def timePF[A, B](pf: PartialFunction[A, B]): PartialFunction[A, B] = ???
  def count: Long = ???
  def mean: Double = ???
  def meanRate: Double = ???
  def update(duration: Long, unit: TimeUnit): Unit = ???
  def snapshot: Snapshot = ???
  def max: Long = ???
  def stdDev: Double = ???
  def fiveMinuteRate: Double = ???
  def min: Long = ???
  def oneMinuteRate: Double = ???
  def fifteenMinuteRate: Double = ???
}
