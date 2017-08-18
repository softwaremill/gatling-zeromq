package com.softwaremill.gatling.zeromq.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class ZmqComponents(zmqProtocol: ZmqProtocol) extends ProtocolComponents {

  override def onStart: Option[Session => Session] = None
  override def onExit: Option[Session => Unit] = None

}
