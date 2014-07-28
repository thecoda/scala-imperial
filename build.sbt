import scala.Some
import scala.util.Try
import bintray.Keys._

organization := "net.thecoda"

lazy val ciBuildNum: Option[String] = Try(Some(sys.env("TRAVIS_BUILD_NUMBER"))) getOrElse None

name := "scala-imperial" + (if(ciBuildNum.isDefined) "-ci" else "")

lazy val semVersion = "0.1.0"

version := ciBuildNum getOrElse semVersion

description := "A scala wrapper for codahale-metrics, with a core focus on improved akka integration"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

crossVersion := CrossVersion.binary

//resolvers ++= Seq(
//  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
//)

lazy val akkaVersion = "2.3.4"

libraryDependencies ++= Seq(
  "com.codahale.metrics" % "metrics-core" % "3.0.2",
  "com.codahale.metrics" % "metrics-healthchecks" % "3.0.2",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)


javacOptions ++= Seq("-Xmx512m", "-Xms128m", "-Xss10m")

javaOptions ++= Seq("-Xmx512m", "-Djava.awt.headless=true")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

//publishTo := {
//  val nexus = "https://oss.sonatype.org/"
//  if (version.value.trim.endsWith("SNAPSHOT"))
//    Some("snapshots" at nexus + "content/repositories/snapshots")
//  else
//    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
//}
//
//credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

pgpPassphrase := Some(Try(sys.env("SECRET")).getOrElse("goaway").toCharArray)

pgpSecretRing := file("./publish/sonatype.asc")

bintrayPublishSettings

repository in bintray := "repo"

bintrayOrganization in bintray := None

publishMavenStyle := true

publishArtifact in Test := false

homepage := Some(url("https://github.com/thecoda/scala-imperial"))

pomExtra := (
  <url>https://github.com/thecoda/scala-imperial</url>
  <scm>
    <url>git@github.com:thecoda/scala-imperial.git</url>
    <connection>scm:git:git@github.com:thecoda/scala-imperial.git</connection>
  </scm>
  <developers>
    <developer>
      <name>Kevin Wright</name>
      <url>https://github.com/kevinwright/</url>
    </developer>
  </developers>
)
