package com.softwaremill.gatling.zeromq.protocol

import akka.actor.ActorSystem
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

object ZmqProtocol {

  def apply(configuration: GatlingConfiguration): ZmqProtocol = ZmqProtocol(
    host = "localhost",
    port = 0
  )

  val ZmqProtocolKey = new ProtocolKey {

    type Protocol = ZmqProtocol
    type Components = ZmqComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] =
      classOf[ZmqProtocol]
        .asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultProtocolValue(configuration: GatlingConfiguration): ZmqProtocol =
      ZmqProtocol(configuration)

    def newComponents(
        system: ActorSystem,
        coreComponents: CoreComponents): ZmqProtocol => ZmqComponents = {
      zmqProtocol =>
        ZmqComponents(zmqProtocol)
    }
  }
}

case class ZmqProtocol(host: String, port: Int) extends Protocol {

  def host(host: String): ZmqProtocol = copy(host = host)
  def port(port: Int): ZmqProtocol = copy(port = port)

}

object SenderType extends Enumeration {
  type SenderType = Value
  val PUB, REQ, PUSH = Value
}
