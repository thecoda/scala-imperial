/*
 * Copyright (c) 2013-2014 Erik van Oosten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package imperial
package mixins

import imperial.wrappers.codahale.CodaHaleBackedArmoury

import scala.language.implicitConversions

import com.codahale.metrics.health.HealthCheck.Result
import com.codahale.metrics.health.{HealthCheck, HealthCheckRegistry}
import org.junit.runner.RunWith
import org.mockito.Mockito.{verify, when}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import scala.util.Try


@RunWith(classOf[JUnitRunner])
class HealthCheckSpec extends FlatSpec {

  "The healthCheck factory method" should "register the created checker" in {
    val checkOwner = newCheckOwner
    val check = checkOwner.createBooleanHealthCheck { true }
    verify(checkOwner.healthcheckRegistry).register("imperial.mixins.CheckOwner.test", check)
  }

  it should "build health checks that call the provided checker" in {
    val mockChecker = mock[SimpleChecker]
    when(mockChecker.check()).thenReturn(true, false, true, false)
    val check = newCheckOwner.createCheckerHealthCheck(mockChecker)
    check.execute() should be (Result.healthy())
    check.execute() should be (Result.unhealthy("FAIL"))
    check.execute() should be (Result.healthy())
    check.execute() should be (Result.unhealthy("FAIL"))
  }

  it should "support a Boolean checker returning true" in {
    val check = newCheckOwner.createBooleanHealthCheck { true }
    check.execute() should be (Result.healthy())
  }

  it should "support a Boolean checker returning false" in {
    val check = newCheckOwner.createBooleanHealthCheck { false }
    check.execute() should be (Result.unhealthy("FAIL"))
  }

  it should "support a Boolean checker returning true implicitly" in {
    val check = newCheckOwner.createImplicitBooleanHealthCheck { Success }
    check.execute() should be (Result.healthy())
  }

  it should "support a Boolean checker returning false implicitly" in {
    val check = newCheckOwner.createImplicitBooleanHealthCheck { Failure }
    check.execute() should be (Result.unhealthy("FAIL"))
  }

  it should "support a Try checker returning Success[Long]" in {
    val check = newCheckOwner.createTryHealthCheck { Try(123L) }
    check.execute() should be (Result.healthy("123"))
  }

  it should "support a Try checker returning Failure" in {
    val exception: IllegalArgumentException = new IllegalArgumentException()
    val check = newCheckOwner.createTryHealthCheck { Try(throw exception) }
    check.execute() should be (Result.unhealthy(exception))
  }

  it should "support an Either checker returning Right[Long]" in {
    val check = newCheckOwner.createEitherHealthCheck { Right(123L) }
    check.execute() should be (Result.healthy("123"))
  }

  it should "support an Either checker returning Left[Boolean]" in {
    val check = newCheckOwner.createEitherHealthCheck { Left(true) }
    check.execute() should be (Result.unhealthy("true"))
  }

  it should "support an Either checker returning Right[String]" in {
    val check = newCheckOwner.createEitherHealthCheck { Right("I am alright") }
    check.execute() should be (Result.healthy("I am alright"))
  }

  it should "support an Either checker returning Left[String]" in {
    val check = newCheckOwner.createEitherHealthCheck { Left("Oops, I am not fine") }
    check.execute() should be (Result.unhealthy("Oops, I am not fine"))
  }

  it should "support an Either checker returning Left[Throwable]" in {
    val exception: IllegalArgumentException = new IllegalArgumentException()
    val check = newCheckOwner.createEitherHealthCheck { Left(exception) }
    check.execute() should be (Result.unhealthy(exception))
  }

  it should "support a Result checker returning Result unchanged" in {
    val result = Result.healthy()
    val check = newCheckOwner.createResultHealthCheck { result }
    check.execute() should be theSameInstanceAs (result)
  }

  it should "support a checker throwing an exception" in {
    val exception: IllegalArgumentException = new IllegalArgumentException()
    val check = newCheckOwner.createThrowingHealthCheck(exception)
    check.execute() should be (Result.unhealthy(exception))
  }

  it should "support an inline Either checker alternating success and failure" in {
    // Tests an inline block because of https://github.com/erikvanoosten/metrics-scala/issues/42 and
    // https://issues.scala-lang.org/browse/SI-3237
    var counter = 0
    val check = newCheckOwner.createEitherHealthCheck {
      counter += 1
      counter match {
        case i if i % 2 == 0 => Right(i)
        case i => Left(i)
      }
    }
    check.execute() should be (Result.unhealthy("1"))
    check.execute() should be (Result.healthy("2"))
  }

  private val newCheckOwner = new CheckOwner()

}

private trait SimpleChecker {
  def check(): Boolean
}

private class CheckOwner() extends Instrumented {
  val healthcheckRegistry: HealthCheckRegistry = mock[HealthCheckRegistry]
  def armoury =  new CodaHaleBackedArmoury(null, healthcheckRegistry) prefixedWith getClass



  // Unfortunately we need a helper method for each supported type. If we wanted a single helper method,
  // we would need to repeat the magnet pattern right here in a test class :(

  def createBooleanHealthCheck(checker: => Boolean): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker }

  def createImplicitBooleanHealthCheck(checker: => Outcome): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker }

  def createTryHealthCheck(checker: => Try[_]): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker }

  def createEitherHealthCheck(checker: => Either[_, _]): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker }

  def createResultHealthCheck(checker: => Result): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker }

  def createThrowingHealthCheck(checkerFailure: => Throwable): HealthCheck =
    armoury.healthCheck("test", "FAIL") {
      def alwaysFails(): Boolean = throw checkerFailure
      alwaysFails()
    }

  def createCheckerHealthCheck(checker: => SimpleChecker): HealthCheck =
    armoury.healthCheck("test", "FAIL") { checker.check() }
}

/** Used to test implicit conversion to boolean. */
private sealed trait Outcome
private case object Success extends Outcome
private case object Failure extends Outcome

/** Implicitly convertible to [[scala.Boolean]]. */
private object Outcome {
  implicit def outcome2Boolean(outcome: Outcome): Boolean = outcome match {
    case Success => true
    case Failure => false
  }
}
