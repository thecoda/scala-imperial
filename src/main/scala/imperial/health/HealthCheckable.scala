package imperial.health

import HealthCheck.Result
import scala.util.{Failure, Success, Try}

trait HealthCheckable[-T] {
  def mkCheck(unhealthyMessage: String, checker: => T): () => Result
}

object HealthCheckable {
  implicit object booleanIsCheckable extends HealthCheckable[Boolean] {
    def mkCheck(unhealthyMessage: String, checker: => Boolean) = () => {
      if (checker) Result.healthy
      else Result.unhealthy(unhealthyMessage)
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Try]].
   */
  implicit object tryIsCheckable extends HealthCheckable[Try[_]] {
    def mkCheck(unhealthyMessage: String, checker: => Try[_]) = () => checker match {
      case Success(m) => Result.healthy(m.toString)
      case Failure(t) => Result.unhealthy(t)
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Either]].
   */
  implicit object eitherIsCheckable extends HealthCheckable[Either[Any,Any]] {
    def mkCheck(unhealthyMessage: String, checker: => Either[Any,Any]) = () => checker match {
      case Right(m) => Result.healthy(m.toString)
      case Left(t: Throwable) => Result.unhealthy(t)
      case Left(m) => Result.unhealthy(m.toString)
    }
  }

  /**
   * Magnet for checkers returning a [[com.codahale.metrics.health.HealthCheck.Result]].
   */
  implicit object resultIsCheckable extends HealthCheckable[Result] {
    def mkCheck(unhealthyMessage: String, checker: => Result) = () => checker
  }


}