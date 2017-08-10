name := "gatling-zeromq"

organization := "com.softwaremill"

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.2.5",
  "org.zeromq" % "jeromq" % "0.4.2"
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
