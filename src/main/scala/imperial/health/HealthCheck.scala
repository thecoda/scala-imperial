package imperial.health

import scala.util.control.NonFatal


object HealthCheck {
  case class Result(isHealthy: Boolean, message: String = "", error: Option[Throwable] = None)
  object Result {
    val healthy: HealthCheck.Result           = Result(true)
    def healthy(msg: String)                  = Result(true, msg)
    def healthy(msg: String, args: String*)   = Result(true, msg.format(args: _*))
    def unhealthy(msg: String)                = Result(false, msg)
    def unhealthy(msg: String, args: String*) = Result(false, msg.format(args: _*))
    def unhealthy(err: Throwable)             = Result(false, err.toString)
  }
}

trait HealthCheck {
  import HealthCheck.Result

  /**
    * Perform the check.
    *
    * @return if the component is healthy, a healthy [[HealthCheck.Result]]; otherwise, an unhealthy
    * [[HealthCheck.Result]] with a descriptive error message or exception
    *
    * @throws Exception if there is an unhandled error during the health check; this will result in
    *                   a failed health check
    */
  def check: () => Result

  /** As for check(), but also catches thrown exceptions and embeds them in the result */
  def execute(): Result = try { check() } catch { case NonFatal(e) => Result.unhealthy(e) }
}

/*
trait HealthCheck {
  import HealthCheck.Result

  /**
   * Perform a check of the application component.
   *
   * @return if the component is healthy, a healthy { @link Result}; otherwise, an unhealthy { @link
   * Result} with a descriptive error message or exception
   * @throws Exception if there is an unhandled error during the health check; this will result in
   *                   a failed health check
   */
  protected def check: Result

  /**
   * Executes the health check, catching and handling any exceptions raised by {@link #check()}.
   *
   * @return if the component is healthy, a healthy { @link Result}; otherwise, an unhealthy { @link
   * Result} with a descriptive error message or exception
   */
  def execute: HealthCheck.Result = {
    try {
      return check
    }
    catch {
      case e: Exception => {
        return Result.unhealthy(e)
      }
    }
  }
}
*/
