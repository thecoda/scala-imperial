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
import org.scalatest.{FlatSpec, OneInstancePerTest}

@RunWith(classOf[JUnitRunner])
class QualifiedNameSpec extends FlatSpec with OneInstancePerTest {

  "The QualifiedName singleton" should "support closures" in {
    val foo: String => QualifiedName = s => QualifiedName(this.getClass)
    foo("").name should equal ("imperial.QualifiedNameSpec")
  }

  it should "support objects" in {
    QualifiedNameSpec.ref.name should equal ("imperial.QualifiedNameSpec")
  }

  it should "support nested objects" in {
    QualifiedNameSpec.nestedRef.name should equal ("imperial.QualifiedNameSpec.Nested")
  }

  it should "support packages" in {
    imperial.subpackage.ref.name should equal ("imperial.subpackage")
  }

  "A QualifiedName instance" should "append names with a period as separator" in {
    QualifiedName(classOf[QualifiedName]).append("part1", "part2").name should equal ("imperial.QualifiedName.part1.part2")
  }

  it should "skip nulls" in {
    QualifiedName(classOf[QualifiedName]).append("part1", null, "part3").name should equal ("imperial.QualifiedName.part1.part3")
  }
}

object QualifiedNameSpec {
  object Nested {
    val ref: QualifiedName = QualifiedName(this.getClass)
  }
  private val ref: QualifiedName = QualifiedName(this.getClass)
  private val nestedRef: QualifiedName = Nested.ref
}
