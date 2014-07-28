package imperial
package mocks

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.TimeUnit
import imperial.measures.{TimerContext, Timer}

class MockTimerContext(timer: Timer) extends TimerContext {
  val start = System.nanoTime()
  override def stop(): Long = {
    val elapsed = System.nanoTime() - start
    timer.update(elapsed, TimeUnit.NANOSECONDS)
    elapsed
  }
}
class MockTimer extends Timer {
  def timerContext: TimerContext = new MockTimerContext(this)

  def update(duration: Long, unit: TimeUnit): Unit = {
    updateNanos(unit.toNanos(duration))
  }

  private def updateNanos(duration: Long) {
    if (duration >= 0) {
      max = math.max(duration, max)
      min = math.min(duration, min)
      count += 1
    }
  }

  var count: Long = 0L
  var max: Long = 0L
  var min: Long = 0L

  def mean: Double = ???
  def meanRate: Double = ???
  def stdDev: Double = ???
  def fiveMinuteRate: Double = ???
  def oneMinuteRate: Double = ???
  def fifteenMinuteRate: Double = ???

  def snapshot: Snapshot = ???
}
