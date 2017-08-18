package com.softwaremill.gatling.zeromq

import com.softwaremill.zeromq.request.builder.ZmqRequestBuilder
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

class Zmq(val requestName: Expression[String],
          val payloads: List[Expression[Any]] = List.empty) {

  def sendMore(expression: Expression[Any]) =
    new Zmq(requestName, payloads :+ expression)

  def send(expression: Expression[Any]) =
    zmqAction(requestName, payloads :+ expression)

  private def zmqAction(requestName: Expression[String],
                        payloads: List[Expression[Any]]): ActionBuilder =
    new ZmqRequestBuilder(requestName, payloads).toActionBuilder()

}
