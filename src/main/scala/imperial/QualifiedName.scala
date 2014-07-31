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

object QualifiedName {

  /**
   * Create a metrics name from a [[Class]].
   *
   * Unlike [[com.codahale.metrics.MetricRegistry.name()]] this version supports Scala classes
   * such as objects and closures.
   *
   * @param metricOwner the class that 'owns' the metric
   * @return a metric (base)name
   */
  def apply(metricOwner: Class[_]): QualifiedName =
    new QualifiedName(removeScalaParts(metricOwner.getName))

  // Example weird class name: TestContext$$anonfun$2$$anonfun$apply$TestObject$2$
  private def removeScalaParts(s: String) = {
    val withReplacements =
      s.replaceAllLiterally("$$anonfun", ".")
      .replaceAllLiterally("$apply", ".")
      .replaceAll( """\$\d*""", ".")
      .replaceAllLiterally(".package.", ".")
    if(withReplacements.endsWith(".")) withReplacements.init else withReplacements
  }


  /**
   * Directly create a metrics name from a [[String]].
   *
   * @param name the (base)name for the metric
   * @param names the name parts to append, `null`s are filtered out
   * @return a metric (base)name
   */
  def apply(name: String, names: String*): QualifiedName = new QualifiedName(name).append(names: _*)
}

/**
 * The (base)name of a metric.
 *
 * Constructed via the companion object, e.g. `MetricName(getClass, "requests")`.
 */
class QualifiedName private (val name: String) {

  /**
   * Extend a metric name.
   *
   * @param names the name parts to append, `null`s are filtered out
   * @return the extended metric name
   */
  def append(names: String*): QualifiedName =
    new QualifiedName((name.split('.') ++ names.filter(_ != null)).filter(_.nonEmpty).mkString("."))

  def +(subname: QualifiedName): QualifiedName = append(subname.name)
}
