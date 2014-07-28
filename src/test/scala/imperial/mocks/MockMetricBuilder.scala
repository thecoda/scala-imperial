package imperial
package mocks

import collection.{mutable => mut}
import imperial.metrics.{Histogram, Gauge}

class MockMetricBuilder extends MetricBuilder  {
  val metricMap: mut.Map[String, Any] = mut.Map.empty
  def getOrFetch[T](k: String)(v: T): T = metricMap.getOrElseUpdate(k, v).asInstanceOf[T]

  def describe: String = {
    metricMap.map{ case (k,v) => s"$k = $v}"}.mkString("\n")
  }

  override def gauge[A](name: String)(f: => A): MockGauge[A] = getOrFetch(name){ new MockGauge(f) }
  override def meter(name: String)    : MockMeter            = getOrFetch(name){ new MockMeter }
  override def timer(name: String)    : MockTimer            = getOrFetch(name){ new MockTimer }
  override def counter(name: String)  : MockCounter          = getOrFetch(name){ new MockCounter }
  override def histogram(name: String): Histogram            = ???
}
