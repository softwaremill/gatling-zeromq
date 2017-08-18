package com.softwaremill.gatling.zeromq

import com.softwaremill.gatling.zeromq.Predef._
import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.util.Random

class SendSimulation extends Simulation {

  private val companies: List[String] =
    List("AAPL", "FB", "GS", "JPM", "TSL", "TWTR")

  private val rnd = new Random()

  val config = zmqConfig
    .host("localhost")
    .port("8916")

  val feeder = Iterator.continually(
    Map(
      "company" -> randomCompany,
      "price" -> randomPrice
    )
  )

  val stockQuotes = scenario("Send stock quotes")
    .feed(feeder)
    .exec(zmq("Stock quote")
      .send("${company}: ${price}"))
    .pause(500 milliseconds, 1 second)

  setUp(
    stockQuotes.inject(
      constantUsersPerSec(10) during (2 seconds)
    )
  ).protocols(config)
    .maxDuration(3 seconds)

  private def randomCompany = companies.get(rnd.nextInt(numberOfCompanies))

  private def randomPrice = 100 + rnd.nextInt(123)

  private def numberOfCompanies = companies.size

}
