package imperial
package wrappers.codahale

import imperial.measures.Histogram
import com.codahale.{metrics => ch}
/**
 * A Scala façade class for Histogram.
 *
 * @see HistogramMetric
 */
class CodaHaleBackedHistogram(val raw: ch.Histogram) extends Histogram {
  def +=(value: Long): Unit = raw update value
  def +=(value: Int): Unit = raw update value

  def count: Long = raw.getCount
  def snapshot: Snapshot = raw.getSnapshot

  def max: Long  = snapshot.getMax
  def min: Long  = snapshot.getMin
  def mean: Double = snapshot.getMean
  def stdDev: Double = snapshot.getStdDev

}
