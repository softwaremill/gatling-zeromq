package com.softwaremill.gatling.zeromq.action

import com.softwaremill.gatling.zeromq.request.builder.ZmqRequest
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import org.zeromq.ZMQ

class ZmqPubAction(sock: ZMQ.Socket,
                   request: ZmqRequest,
                   coreComponents: CoreComponents,
                   throttled: Boolean,
                   next: Action)
    extends ZmqAction(sock, request, coreComponents, throttled, next) {

  override val name: String = genName("zmqPub")

  override protected def doSend(session: Session,
                                requestName: String,
                                payloads: List[Any]): Boolean = {
    sendAll(payloads).forall(_.booleanValue())
  }

}
