package imperial.wrappers.codahale

import imperial.measures.Counter
import com.codahale.{metrics => ch}

/** A Scala façade class for Counter. */
class CodaHaleBackedCounter(val raw: ch.Counter) extends Counter {
  def count[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B] =
    new Counter.CountingPf(this, pf)

  def +=(delta: Long): Unit = raw inc delta
  def -=(delta: Long): Unit = raw dec delta
  def inc(delta: Long = 1): Unit = raw inc delta
  def dec(delta: Long = 1): Unit = raw dec delta

  def count: Long = raw.getCount
}
