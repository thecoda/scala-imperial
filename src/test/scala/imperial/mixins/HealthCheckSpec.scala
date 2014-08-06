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

import org.junit.runner.RunWith
import org.mockito.Mockito.{verify, when}
import org.mockito.Matchers.{eq => meq, any}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._
import scala.util.Try

import imperial.health.{HealthCheck, HealthCheckable}
import HealthCheck.Result
import scala.util.control.NoStackTrace

object HealthCheckSpec {
  private trait SimpleChecker { def check(): Boolean }

  private class CheckOwner() extends mocks.MockitoInstrumented {
    /** Simple helper allowing checks with a default name/message to be created outside the object */
    def mkCheck[T: HealthCheckable](payload: => T): HealthCheck = armoury.healthCheck("test", "FAIL") {
      payload
    }
  }

  /** Used to test implicit conversion to boolean. */
  sealed trait Outcome
  case object SuccessOutcome extends Outcome
  case object FailureOutcome extends Outcome
}


@RunWith(classOf[JUnitRunner])
class HealthCheckSpec extends FlatSpec {

  import HealthCheckSpec._

  implicit object outcomeIsCheckable extends HealthCheckable[Outcome] {
    def mkCheck(unhealthyMessage: String, outcome: => Outcome) = () => {
      if (outcome == SuccessOutcome) Result.healthy
      else Result.unhealthy(unhealthyMessage)
    }
  }

  val sampleException: IllegalArgumentException = new IllegalArgumentException with NoStackTrace


  "The healthCheck factory method" should "register the created checker" in {
    val checkOwner = newCheckOwner
    val check = checkOwner.mkCheck{ true }
    verify(checkOwner.healthCheckRegistry).register(meq("imperial.mixins.HealthCheckSpec.CheckOwner.test"), any[com.codahale.metrics.health.HealthCheck])
  }

  it should "build health checks that call the provided checker" in {
    val mockChecker = mock[SimpleChecker]
    when(mockChecker.check()).thenReturn(true, false, true, false)
    val check = newCheckOwner.mkCheck(mockChecker.check())
    check.execute() should be (Result.healthy)
    check.execute() should be (Result.unhealthy("FAIL"))
    check.execute() should be (Result.healthy)
    check.execute() should be (Result.unhealthy("FAIL"))
  }

  it should "support a Boolean checker returning true" in {
    runCheck { true } should be (Result.healthy)
  }

  it should "support a Boolean checker returning false" in {
    runCheck { false } should be (Result.unhealthy("FAIL"))
  }

  it should "support a locally-defined checkable delegating to boolean true" in {
    runCheck { SuccessOutcome } should be (Result.healthy)
  }

  it should "support a locally-defined checkable delegating to boolean false" in {
    runCheck { FailureOutcome } should be (Result.unhealthy("FAIL"))
  }

  it should "support a Try checker returning Success[Long]" in {
    runCheck { Try(123L) } should be (Result.healthy("123"))
  }

  it should "support a Try checker returning Failure" in {
    runCheck { Try(throw sampleException) } should be (Result.unhealthy(sampleException))
  }

  it should "support an Either checker returning Right[Long]" in {
    runCheck { Right(123L) } should be (Result.healthy("123"))
  }

  it should "support an Either checker returning Left[Boolean]" in {
    runCheck { Left(true) } should be (Result.unhealthy("true"))
  }

  it should "support an Either checker returning Right[String]" in {
    runCheck { Right("I am alright") } should be (Result.healthy("I am alright"))
  }

  it should "support an Either checker returning Left[String]" in {
    runCheck { Left("Oops, I am not fine") } should be (Result.unhealthy("Oops, I am not fine"))
  }

  it should "support an Either checker returning Left[Throwable]" in {
    runCheck { Left(sampleException) } should be (Result.unhealthy(sampleException))
  }

  it should "support a Result checker returning Result unchanged" in {
    val result = Result.healthy
    val check = runCheck { result }
    check should be theSameInstanceAs (result)
  }

  it should "support a checker throwing an exception" in {
    runCheck {
      def alwaysFails(): Boolean = throw sampleException
      alwaysFails()
    } should be (Result.unhealthy(sampleException))
  }

  it should "support an inline Either checker alternating success and failure" in {
    // Tests an inline block because of https://github.com/erikvanoosten/metrics-scala/issues/42 and
    // https://issues.scala-lang.org/browse/SI-3237

    val counter = Iterator from 1
    val check = newCheckOwner.mkCheck {
      counter.next() match {
        case i if i % 2 == 0 => Right(i)
        case i => Left(i)
      }
    }
    check.execute() should be (Result.unhealthy("1"))
    check.execute() should be (Result.healthy("2"))
  }

  private val newCheckOwner = new CheckOwner()
  def runCheck[T: HealthCheckable](payload: => T): Result = newCheckOwner.mkCheck(payload).execute()


}

