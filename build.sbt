enablePlugins(GatlingPlugin)

name := "gatling-zeromq"

organization := "com.softwaremill"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

scalafmtOnCompile := true
scalafmtVersion := "1.1.0"

scmInfo := Some(
  ScmInfo(url("https://github.com/softwaremill/gatling-zeromq"),
          "scm:git:git@github.com/softwaremill/gatling-zeromq.git"))

developers := List(
  Developer("mchmielarz",
            "Michał Chmielarz",
            "",
            url("https://softwaremill.com")))

licenses := ("Apache-2.0",
             url("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil

homepage := Some(url("http://softwaremill.com/open-source"))

val gatlingVer = "2.2.5"
val zeromqVer = "0.4.2"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % gatlingVer,
  "org.zeromq" % "jeromq" % zeromqVer,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVer % "test",
  "io.gatling" % "gatling-test-framework" % gatlingVer % "test"
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
