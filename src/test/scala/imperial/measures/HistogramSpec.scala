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

package imperial.measures

import org.junit.runner.RunWith
import org.mockito.Mockito.{verify, when}
import org.scalatest.Matchers._
import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class HistogramSpec extends FlatSpec with OneInstancePerTest {
  val metric = mock[com.codahale.metrics.Histogram]
  val histogram = Histogram(metric)

  "A histogram" should "updates the underlying histogram with an int" in {
    histogram += 12
    verify(metric).update(12)
  }

  it should "update the underlying histogram with a long" in {
    histogram += 12L
    verify(metric).update(12L)
  }

  it should "retrieve a snapshot for statistics" in {
    val snapshot = mock[com.codahale.metrics.Snapshot]
    when(snapshot.getMax).thenReturn(1L)
    when(metric.getSnapshot).thenReturn(snapshot)
    histogram.max should equal (1L)
  }
}
