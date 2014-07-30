package imperial

import scala.language.implicitConversions

import com.codahale.metrics.health.HealthCheck.Result
import com.codahale.metrics.health.HealthCheck

import scala.util.{Failure, Success, Try}



/**
 * Magnet for the checker.
 * See [[http://spray.io/blog/2012-12-13-the-magnet-pattern/]].
 */
sealed trait HealthCheckMagnet {
  def apply(unhealthyMessage: String): HealthCheck
}

//TODO: type classes!

object HealthCheckMagnet {
  /**
   * Magnet for checkers returning a [[scala.Boolean]] (possibly implicitly converted).
   */
  implicit def fromBooleanCheck[A <% Boolean](checker: => A) = new HealthCheckMagnet {
    def apply(unhealthyMessage: String) = new HealthCheck() {
      protected def check: Result =
        if (checker) Result.healthy()
        else Result.unhealthy(unhealthyMessage)
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Try]].
   */
  implicit def fromTryChecker(checker: => Try[_]) = new HealthCheckMagnet {
    def apply(unhealthyMessage: String) = new HealthCheck() {
      protected def check: Result = checker match {
        case Success(m) => Result.healthy(m.toString)
        case Failure(t) => Result.unhealthy(t)
      }
    }
  }

  /**
   * Magnet for checkers returning an [[scala.util.Either]].
   */
  implicit def fromEitherChecker(checker: => Either[_, _]) = new HealthCheckMagnet {
    def apply(unhealthyMessage: String) = new HealthCheck() {
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
  implicit def fromMetricsResultCheck(checker: => Result) = new HealthCheckMagnet {
    def apply(unhealthyMessage: String) = new HealthCheck() {
      protected def check: Result = checker
    }
  }
}
