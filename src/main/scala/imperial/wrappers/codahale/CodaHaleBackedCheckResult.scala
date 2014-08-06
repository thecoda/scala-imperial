package imperial
package wrappers.codahale

import com.codahale.metrics.health.HealthCheck.{Result => CHResult}
import imperial.health.HealthCheck


object CodaHaleBackedCheckResult {
  def apply(raw: CHResult): HealthCheck.Result = HealthCheck.Result(raw.isHealthy, raw.getMessage, Option(raw.getError))
  val healthy: HealthCheck.Result = apply(CHResult.healthy)
  def healthy(msg: String) = apply(CHResult.healthy(msg))
  def healthy(msg: String, args: String*) = apply(CHResult.healthy(msg, args: _*))
  def unhealthy(msg: String) = apply(CHResult.unhealthy(msg))
  def unhealthy(msg: String, args: String*) = apply(CHResult.unhealthy(msg, args: _*))
  def unhealthy(err: Throwable) = apply(CHResult.unhealthy(err))
}
