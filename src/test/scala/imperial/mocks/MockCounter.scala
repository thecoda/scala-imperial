package imperial
package mocks

import imperial.measures.Counter

class MockCounter extends Counter {
  var count: Long = 0

  def count[A, B](pf: PartialFunction[A, B]): PartialFunction[A, B] =
    new Counter.CountingPf[A,B](this, pf)

  def -=(delta: Long): Unit = count -= delta
  def +=(delta: Long): Unit = count += delta
  def dec(delta: Long): Unit = count -= delta
  def inc(delta: Long): Unit = count += delta
}
