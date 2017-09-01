package com.softwaremill.gatling.zeromq

import com.softwaremill.gatling.zeromq.Predef._
import com.softwaremill.gatling.zeromq.support.{Server, Socket}
import io.gatling.core.Predef._
import org.zeromq.ZMQ
import zmq.util.Utils

import scala.concurrent.duration._
import scala.language.postfixOps

class RequestSimulation extends Simulation {

  val port = Utils.findOpenPort()

  val server = new Server(new Socket(port, ZMQ.REP))

  before {
    server.run()
  }

  after {
    server.stop()
  }

  val config = zmqConfig
    .host("localhost")
    .port(port)

  val feeder: Iterator[Map[String, Any]] = Iterator.continually(
    Map(
      "company" -> companies,
      "price" -> prices
    )
  )

  val stockQuotes = scenario("Send stock quotes")
    .feed(feeder)
    .exec(zmqReq("Stock quote")
      .send("${company.random()}: ${price.random()}"))
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
