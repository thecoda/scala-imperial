package imperial.wrappers.codahale

import imperial.measures.Meter
import com.codahale.{metrics => ch}

class CodaHaleBackedMeter(val raw: ch.Meter) extends Meter {

  def mark(): Unit = raw.mark()
  def mark(count: Long): Unit = raw mark count

  def count: Long = raw.getCount

  def fifteenMinuteRate: Double = raw.getFifteenMinuteRate
  def fiveMinuteRate: Double = raw.getFiveMinuteRate
  def oneMinuteRate: Double = raw.getOneMinuteRate
  def meanRate: Double = raw.getMeanRate
}
