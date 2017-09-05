package com.softwaremill.gatling.zeromq.action

import com.softwaremill.gatling.zeromq.request.builder.ZmqRequest
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import org.zeromq.ZMQ

class ZmqPushAction(sock: ZMQ.Socket,
                    request: ZmqRequest,
                    coreComponents: CoreComponents,
                    throttled: Boolean,
                    next: Action)
    extends ZmqAction(sock, request, coreComponents, throttled, next) {

  override val name: String = genName("zmqPush")

}
