package com.softwaremill.gatling.zeromq.protocol

import io.gatling.core.config.GatlingConfiguration

import scala.language.implicitConversions

object ZmqProtocolBuilder {

  implicit def toZmqProtocol(builder: ZmqProtocolBuilder): ZmqProtocol =
    builder.build

  def apply(configuration: GatlingConfiguration): ZmqProtocolBuilder =
    ZmqProtocolBuilder(ZmqProtocol(configuration))

}

case class ZmqProtocolBuilder(zmqProtocol: ZmqProtocol) {

  def build = zmqProtocol

}
