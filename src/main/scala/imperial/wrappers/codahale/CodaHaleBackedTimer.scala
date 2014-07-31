package imperial
package wrappers.codahale

import java.util.concurrent.TimeUnit
import imperial.measures.{Timer, TimerContext}
import com.codahale.{metrics => ch}

class CodaHaleBackedTimer(val raw: ch.Timer) extends Timer{

  def update(duration: Long, unit: TimeUnit): Unit = raw.update(duration, unit)

  def timerContext(): TimerContext = new CodaHaleBackedTimerContext(raw.time())

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
