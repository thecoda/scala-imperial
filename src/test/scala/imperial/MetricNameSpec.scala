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

import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, OneInstancePerTest}

@RunWith(classOf[JUnitRunner])
class MetricNameSpec extends FunSpec with OneInstancePerTest {

  describe("MetricName object") {
    it("concatenates names with a period as separator") {
      MetricName(classOf[MetricName], "part1", "part2").name should equal ("imperial.MetricName.part1.part2")
    }

    it("skips nulls") {
      MetricName(classOf[MetricName], "part1", null, "part3").name should equal ("imperial.MetricName.part1.part3")
    }

    it("supports closures") {
      val foo: String => MetricName = s => MetricName(this.getClass)
      foo("").name should equal ("imperial.MetricNameSpec")
    }

    it("supports objects") {
      MetricNameSpec.ref.name should equal ("imperial.MetricNameSpec")
    }

    it("supports nested objects") {
      MetricNameSpec.nestedRef.name should equal ("imperial.MetricNameSpec.Nested")
    }

    it("supports packages") {
      imperial.subpackage.ref.name should equal ("imperial.subpackage")
    }
  }

  describe("MetricName") {
    it("appends names with a period as separator") {
      MetricName(classOf[MetricName]).append("part1", "part2").name should equal ("imperial.MetricName.part1.part2")
    }

    it("skips nulls") {
      MetricName(classOf[MetricName]).append("part1", null, "part3").name should equal ("imperial.MetricName.part1.part3")
    }
  }
}

object MetricNameSpec {
  object Nested {
    val ref: MetricName = MetricName(this.getClass)
  }
  private val ref: MetricName = MetricName(this.getClass)
  private val nestedRef: MetricName = Nested.ref
}
