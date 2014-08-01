package imperial.health

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import scala.util.{Failure, Success, Try}

trait HealthCheckable[-T] {
  def mkHealthCheck(unhealthyMessage: String, checker: => T): HealthCheck
}

object HealthCheckable {
  implicit object booleanIsCheckable extends HealthCheckable[Boolean] {
    def mkHealthCheck(unhealthyMessage: String, checker: => Boolean) = new HealthCheck() {
      protected def check: Result =
        if (checker) Result.healthy()
        else Result.unhealthy(unhealthyMessage)
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Try]].
   */
  implicit object tryIsCheckable extends HealthCheckable[Try[_]] {
    def mkHealthCheck(unhealthyMessage: String, checker: => Try[_]) = new HealthCheck() {
      protected def check: Result = checker match {
        case Success(m) => Result.healthy(m.toString)
        case Failure(t) => Result.unhealthy(t)
      }
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Either]].
   */
  implicit object eitherIsCheckable extends HealthCheckable[Either[Any,Any]] {
    def mkHealthCheck(unhealthyMessage: String, checker: => Either[Any,Any]) = new HealthCheck() {
      protected def check: Result = checker match {
        case Right(m) => Result.healthy(m.toString)
        case Left(t: Throwable) => Result.unhealthy(t)
        case Left(m) => Result.unhealthy(m.toString)
      }
    }
  }

  /**
   * Magnet for checkers returning a [[com.codahale.metrics.health.HealthCheck.Result]].
   */
  implicit object resultIsCheckable extends HealthCheckable[Result] {
    def mkHealthCheck(unhealthyMessage: String, checker: => Result) = new HealthCheck() {
      protected def check: Result = checker
    }
  }


}