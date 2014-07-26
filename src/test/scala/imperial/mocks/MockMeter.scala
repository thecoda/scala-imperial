package imperial.mocks

import imperial.Meter

class MockMeter extends Meter {
  var count: Long = 0L

  def mark(): Unit = count += 1
  def mark(delta: Long): Unit = count += delta

  def meanRate: Double = ???

  def oneMinuteRate: Double = ???
  def fiveMinuteRate: Double = ???
  def fifteenMinuteRate: Double = ???
}
