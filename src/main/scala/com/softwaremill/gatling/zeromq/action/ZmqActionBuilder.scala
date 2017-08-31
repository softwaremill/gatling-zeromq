package com.softwaremill.gatling.zeromq.action

import com.softwaremill.gatling.zeromq.protocol.{
  SenderType,
  ZmqComponents,
  ZmqProtocol
}
import com.softwaremill.gatling.zeromq.request.builder.ZmqRequestBuilder
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.zeromq.ZMQ

class ZmqActionBuilder(val zmqRequestBuilder: ZmqRequestBuilder)
    extends ActionBuilder {

  val ONE_THREAD = 1

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx.{coreComponents, protocolComponentsRegistry, system, throttled}

    val zmqComponents: ZmqComponents =
      protocolComponentsRegistry.components(ZmqProtocol.ZmqProtocolKey)

    val zmqCtx = ZMQ.context(ONE_THREAD)

    val sock = zmqRequestBuilder.senderType match {
      case SenderType.REQ => zmqCtx.socket(ZMQ.REQ)
      case _              => zmqCtx.socket(ZMQ.PUB)
    }

    val host = zmqComponents.zmqProtocol.host
    val port = zmqComponents.zmqProtocol.port

    sock.connect(s"tcp://$host:$port")

    system.registerOnTermination(() => {
      sock.close()
      zmqCtx.term()
    })

    val request = zmqRequestBuilder.build()

    zmqRequestBuilder.senderType match {
      case SenderType.REQ =>
        new ZmqReqAction(sock, request, coreComponents, throttled, next)
      case _ =>
        new ZmqPubAction(sock, request, coreComponents, throttled, next)
    }
  }

}
