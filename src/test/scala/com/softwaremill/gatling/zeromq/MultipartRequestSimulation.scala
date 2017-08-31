package com.softwaremill.gatling.zeromq

import com.softwaremill.gatling.zeromq.Predef._
import com.softwaremill.gatling.zeromq.support.{Server, Socket}
import io.gatling.core.Predef._
import org.zeromq.ZMQ
import zmq.util.Utils

import scala.concurrent.duration._
import scala.language.postfixOps

class MultipartRequestSimulation extends Simulation {

  val messageCount = 2
  val port = Utils.findOpenPort()

  val server = new Server(new Socket(port, ZMQ.REP, messageCount))

  before {
    server.run()
  }

  after {
    server.stop()
  }

  val config = zmqConfig
    .host("localhost")
    .port(port.toString)

  val feeder: Iterator[Map[String, Any]] = Iterator.continually(
    Map(
      "company" -> companies,
      "price" -> prices
    )
  )

  val stockQuotes = scenario("Send stock quotes")
    .feed(feeder)
    .exec(
      zmqReq("Stock quote")
        .sendMore("${company.random()}")
        .send("${price.random()}"))
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
