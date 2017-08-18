package com.softwaremill.gatling.zeromq.request.builder

import io.gatling.core.session.Expression

case class ZmqRequest(name: Expression[String], payloads: List[Expression[Any]])
