package com.softwaremill.gatling.zeromq.action

import com.softwaremill.gatling.zeromq.request.builder.ZmqRequest
import io.gatling.commons.stats.OK
import io.gatling.commons.util.ClockSingleton.nowMillis
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.message.ResponseTimings
import org.zeromq.{ZMQ, ZMQException}

class ZmqReqAction(sock: ZMQ.Socket,
                   request: ZmqRequest,
                   coreComponents: CoreComponents,
                   throttled: Boolean,
                   next: Action)
    extends ZmqAction(sock, request, coreComponents, throttled, next) {

  override val name: String = genName("zmqReq")

  override protected def doSend(session: Session,
                                requestName: String,
                                payloads: List[Any]): Boolean = {
    val isEverythingSent = sendAll(payloads).foldLeft(true)(_ && _)
    try {
      if (isEverythingSent) {
        val responseStartDate = nowMillis
        val resp = sock.recvStr()
        val responseEndDate = nowMillis
        statsEngine.logResponse(
          session,
          requestName,
          ResponseTimings(responseStartDate, responseEndDate),
          OK,
          None,
          Some(resp))
      }
    } catch {
      case zmqe: ZMQException =>
        statsEngine.logCrash(session,
                             requestName,
                             s"${zmqe.getErrorCode} ${zmqe.getMessage}")
    }
    isEverythingSent
  }

}
