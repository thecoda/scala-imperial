import scala.util.Try
import bintray.Keys._

// See crossrelease.sh for valid combinations of akkaVersion and crossScalaVersion.

// Akka versions: 2.1.4, 2.2.3, 2.3.2
akkaVersion := Try(sys.env("AKKA_VERSION")).getOrElse("2.3.4")

organization := "net.thecoda"

name := "scala-imperial"

lazy val baseVersion = "0.1.0"

version <<= (akkaVersion) { av =>
  val akkaVersion = if (av.nonEmpty) "_a" + av.split('.').take(2).mkString(".") else ""
  baseVersion + akkaVersion
}

description <<= (scalaVersion, akkaVersion) { (sv, av) =>
  val akkaDescription = if (av.nonEmpty) "Akka " + av +" and " else ""
  "scala-imperial for " + akkaDescription + "Scala " + sbt.cross.CrossVersionUtil.binaryScalaVersion(sv)
}

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

crossVersion := CrossVersion.binary

resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

libraryDependencies ++= Seq(
  "com.codahale.metrics" % "metrics-core" % "3.0.2",
  "com.codahale.metrics" % "metrics-healthchecks" % "3.0.2",
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

libraryDependencies <++= (akkaVersion) { av =>
  if (av.nonEmpty)
    Seq(
      "com.typesafe.akka" %% "akka-actor" % av,
      "com.typesafe.akka" %% "akka-testkit" % av % "test"
    )
  else
    Seq()
}

unmanagedSourceDirectories in Compile <<= (unmanagedSourceDirectories in Compile, sourceDirectory in Compile, akkaVersion) { (sds: Seq[java.io.File], sd: java.io.File, av: String) =>
  val extra = new java.io.File(sd, "akka")
  (if (av.nonEmpty && extra.exists) Seq(extra) else Seq()) ++ sds
}

unmanagedSourceDirectories in Test <<= (unmanagedSourceDirectories in Test, sourceDirectory in Test, akkaVersion) { (sds: Seq[java.io.File], sd: java.io.File, av: String) =>
  val extra = new java.io.File(sd, "akka")
  (if (av.nonEmpty && extra.exists) Seq(extra) else Seq()) ++ sds
}

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
  <url>https://github.com/thecoda/imperial-scala</url>
  <scm>
    <url>git@github.com:thecoda/imperial-scala.git</url>
    <connection>scm:git:git@github.com:thecoda/imperial-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <name>Kevin Wright</name>
      <url>https://github.com/kevinwright/</url>
    </developer>
  </developers>
)
