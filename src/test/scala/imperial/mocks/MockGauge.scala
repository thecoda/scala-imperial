package imperial
package mocks

import imperial.measures.Gauge

class MockGauge[A](f: => A) extends Gauge[A] {
  def value: A = f
}
