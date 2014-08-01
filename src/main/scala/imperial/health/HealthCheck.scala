package imperial.health

/*
object HealthCheck {
  case class Result(isHealthy: Boolean, message: String, error: Option[Throwable])
}

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
