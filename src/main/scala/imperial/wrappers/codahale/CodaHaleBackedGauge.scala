package imperial.wrappers.codahale

import imperial.measures.Gauge
import com.codahale.{metrics => ch}

/** A Scala façade class for Gauge. */
class CodaHaleBackedGauge[T](val raw: ch.Gauge[T]) extends Gauge[T] {
  def value: T = raw.getValue
}
