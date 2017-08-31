package com.softwaremill.gatling.zeromq

import com.softwaremill.gatling.zeromq.Predef.{zmqPub, zmqConfig}
import io.gatling.core.Predef._

import scala.concurrent.duration._

import scala.language.postfixOps

class MultipartPublisherSimulation extends Simulation {

  val config = zmqConfig
    .host("localhost")
    .port("8916")

  val feeder: Iterator[Map[String, Any]] = Iterator.continually(
    Map(
      "company" -> companies,
      "price" -> prices
    )
  )

  val stockQuotes = scenario("Send stock quotes")
    .feed(feeder)
    .exec(
      zmqPub("Stock quote")
        .sendMore("${company.random()}")
        .send("${price.random()}")
    )
    .pause(500 milliseconds, 1 second)

  setUp(
    stockQuotes.inject(
      constantUsersPerSec(10) during (2 seconds)
    )
  ).protocols(config)
    .maxDuration(3 seconds)

  private def companies = List("AAPL", "FB", "GS", "JPM", "TSL", "TWTR")

  private def prices = (100 until 223).toList
}
