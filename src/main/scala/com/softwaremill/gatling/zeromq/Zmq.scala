package com.softwaremill.gatling.zeromq

import com.softwaremill.gatling.zeromq.protocol.SenderType.SenderType
import com.softwaremill.gatling.zeromq.request.builder.ZmqRequestBuilder
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

class Zmq(val requestName: Expression[String],
          val senderType: SenderType,
          val payloads: List[Expression[Any]] = List.empty) {

  def sendMore(expression: Expression[Any]) =
    new Zmq(requestName, senderType, payloads :+ expression)

  def send(expression: Expression[Any]) =
    zmqAction(requestName, senderType, payloads :+ expression)

  private def zmqAction(requestName: Expression[String],
                        senderType: SenderType,
                        payloads: List[Expression[Any]]): ActionBuilder =
    new ZmqRequestBuilder(requestName, senderType, payloads).toActionBuilder()

}
