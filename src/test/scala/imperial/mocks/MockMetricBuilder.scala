package imperial
package mocks

class MockMetricBuilder extends MetricBuilder  {
  override def gauge[A](name: String, scope: String)(f: => A): Gauge[A] = ???
  override def meter(name: String, scope: String): Meter = ???
  override def timer(name: String, scope: String): Timer = ???
  override def counter(name: String, scope: String): Counter = ???
  override def histogram(name: String, scope: String): Histogram = ???
}
