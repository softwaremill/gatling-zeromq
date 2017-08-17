package com.softwaremill.zeromq.action

import com.softwaremill.zeromq.protocol.{ZmqComponents, ZmqProtocol}
import com.softwaremill.zeromq.request.builder.ZmqRequestBuilder
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.zeromq.ZMQ

class ZmqSendActionBuilder(val zmqRequestBuilder: ZmqRequestBuilder)
    extends ActionBuilder {

  val ONE_THREAD = 1

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx.{coreComponents, protocolComponentsRegistry, system, throttled}

    val zmqComponents: ZmqComponents =
      protocolComponentsRegistry.components(ZmqProtocol.ZmqProtocolKey)

    val zmqCtx = ZMQ.context(ONE_THREAD)
    val sock = zmqCtx.socket(ZMQ.PUB)

    val host = zmqComponents.zmqProtocol.host
    val port = zmqComponents.zmqProtocol.port

    sock.connect(s"tcp://$host:$port")

    system.registerOnTermination(() => {
      sock.close()
      zmqCtx.term()
    })

    val request = zmqRequestBuilder.build()

    new ZmqSendAction(sock, request, coreComponents, throttled, next)
  }

}
