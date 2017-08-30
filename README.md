# gatling-zeromq

[![Build Status](https://travis-ci.org/softwaremill/gatling-zeromq.svg?branch=master)](https://travis-ci.org/softwaremill/gatling-zeromq)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.softwaremill.gatling-zeromq/gatling-zeromq_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.softwaremill.gatling-zeromq/gatling-zeromq_2.11)
[![Dependencies](https://app.updateimpact.com/badge/636824687711752192/gatling-zeromq.svg?config=compile)](https://app.updateimpact.com/latest/636824687711752192/gatling-zeromq)

## Goal of the project

The aim of the project is to allow load testing of applications using [ZeroMQ](http://zeromq.org) with the help of the [Gatling tool](http://gatling.io).

## Adding gatling-zeromq to your project

SBT dependency:

```scala
"com.softwaremill.gatling-zeromq" %% "gatling-zeromq" % "0.1.0"
```

Gradle dependency:
```groovy
"com.softwaremill.gatling-zeromq:gatling-zeromq_2.11:0.1.0"
```

`gatling-zeromq` is available for Scala 2.11 and requires Java 8.

You need to add a dependency on Gatling tool to your project since it is not bundled in this library.

## Running tests

Tests in the project can be started with:
```scala
sbt gatling:test
```

For more information look at the [Gatling SBT plugin](http://gatling.io/docs/current/extensions/sbt_plugin/)

## ZeroMQ connector

The project uses [JeroMQ](https://github.com/zeromq/jeromq) as the ZeroMQ connector.

## Configuration

Before running a scenario, you have to provide host and port to connect to. For example:
```
  val config = zmqConfig
    .host("localhost")
    .port("8916")
```

## Message patterns

The plugin supports the publishing side of the PUB-SUB pattern, i.e. an application under load tests subscribes to data coming from ZeroMQ.

## Sending data

Sending of a message and a multi-part message is supported:
```
scenario("Scenario A")
    .exec(
        zmq("a request")
            .send("a message")
    )

scenario("Scenario B")
    .exec(
        zmq("a request")
            .sendMore("a multi")
            .sendMore("part")
            .send("message")
    )
```

Types of data can be `String`, `Array[Byte]`, numeric ones and [Gatling's Expression Language strings](http://gatling.io/docs/current/session/expression_el/#el) as well.

## Contributing

Take a look at the [open issues](https://github.com/softwaremill/gatling-zeromq/issues) and pick a task you'd like to work on!
