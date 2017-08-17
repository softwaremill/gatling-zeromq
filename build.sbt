enablePlugins(GatlingPlugin)

name := "gatling-zeromq"

organization := "com.softwaremill"

version := "0.1.0"

scalaVersion := "2.11.11"

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
