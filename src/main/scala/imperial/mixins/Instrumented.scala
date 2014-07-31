package imperial
package mixins

/**
 * The mixin trait for creating a class which is instrumented with metrics.
 *
 * Use it as follows:
 * {{{
 * object Application {
 *   // The application wide metrics registry.
 *   val metricRegistry = new com.codahale.metrics.MetricRegistry()
 * }
 * trait Instrumented extends InstrumentedBuilder {
 *   val metricRegistry = Application.metricRegistry
 * }
 *
 * class Example(db: Database) extends Instrumented {
 *   private[this] val loading = metrics.timer("loading")
 *
 *   def loadStuff(): Seq[Row] = loading.time {
 *     db.fetchRows()
 *   }
 * }
 * }}}
 *
 * It is also possible to override the metric base name. For example:
 * {{{
 * class Example(db: Database) extends Instrumented {
 *   override lazy val metricBaseName = MetricName("Overridden.Base.Name")
 *   private[this] val loading = metrics.timer("loading")
 *
 *   def loadStuff(): Seq[Row] = loading.time {
 *     db.fetchRows()
 *   }
 * }
 * }}}
 */
trait Instrumented {
  /** The Armoury where created measures are registered. */
  def armoury: Armoury
}
