package imperial
package wrappers.codahale

import com.codahale.metrics.health.HealthCheck.{Result => CHResult}

class CodaHaleBackedCheckResult(raw: CHResult) extends CheckResult {
  def isHealthy: Boolean = raw.isHealthy
  def message: String = Some(raw.getMessage) getOrElse ""
  def error: Option[Throwable]  = Some(raw.getError)
}

object CodaHaleBackedCheckResult {
  def apply(raw: CHResult) = new CodaHaleBackedCheckResult(raw)
  val healthy: CheckResult = apply(CHResult.healthy)
  def healthy(msg: String) = apply(CHResult.healthy(msg))
  def healthy(msg: String, args: String*) = apply(CHResult.healthy(msg, args: _*))
  def unhealthy(msg: String) = apply(CHResult.unhealthy(msg))
  def unhealthy(msg: String, args: String*) = apply(CHResult.unhealthy(msg, args: _*))
  def unhealthy(err: Throwable) = apply(CHResult.unhealthy(err))
}
