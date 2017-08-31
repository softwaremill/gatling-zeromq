package com.softwaremill.gatling.zeromq.action

import com.softwaremill.gatling.zeromq.request.builder.ZmqRequest
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.ClockSingleton._
import io.gatling.commons.validation.{Failure, Success, Validation, _}
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.{Session, _}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.zeromq.{ZMQ, ZMQException}

import scala.annotation.tailrec
import scala.util.control.NonFatal

abstract class ZmqAction(val sock: ZMQ.Socket,
                         val request: ZmqRequest,
                         val coreComponents: CoreComponents,
                         val throttled: Boolean,
                         val next: Action)
    extends ExitableAction
    with NameGen {

  val statsEngine = coreComponents.statsEngine

  override val name: String = genName("zmqRequest")

  protected def doSend(session: Session,
                       requestName: String,
                       payloads: List[Any]): Boolean = {
    sendAll(payloads).forall(_.booleanValue())
  }

  override def execute(session: Session): Unit = recover(session) {
    request
      .name(session)
      .map(requestName => {
        sendPayloads(session, requestName)
      })
      .map(_ =>
        if (throttled) {
          coreComponents.throttler.throttle(session.scenario,
                                            () => next ! session)
        } else {
          next ! session
      })
  }

  private def sendPayloads(session: Session, requestName: String): Unit = {
    resolved(request.payloads, session) match {
      case Failure(msg) => {
        logUnbuildableRequest(session,
                              requestName,
                              s"Cannot resolve request payloads: $msg")
      }
      case Success(payloads) => {
        try {
          val requestStartDate = nowMillis
          val isEverythingSent = doSend(session, requestName, payloads)
          val requestEndDate = nowMillis

          logAction(session,
                    requestName,
                    isEverythingSent,
                    requestStartDate,
                    requestEndDate)
        } catch {
          case zmqe: ZMQException =>
            logError(session,
                     requestName,
                     s"${zmqe.getMessage}: ${zmqe.getErrorCode}")
        }
      }
    }
  }

  def resolved(payloads: List[Expression[Any]],
               session: Session): Validation[List[Any]] = {
    @tailrec
    def resolveRec(session: Session,
                   entries: Iterator[Expression[Any]],
                   acc: List[Any]): Validation[List[Any]] = {
      if (entries.isEmpty)
        acc.reverse.success
      else {
        entries.next()(session) match {
          case Success(value) =>
            resolveRec(session, entries, value :: acc)
          case failure: Failure => failure
        }
      }
    }

    try resolveRec(session, payloads.iterator, Nil)
    catch {
      case NonFatal(e) => Failure(e.getMessage)
    }

  }

  private def logAction(session: Session,
                        requestName: String,
                        isEverythingSent: Boolean,
                        requestStartDate: Long,
                        requestEndDate: Long) = {
    statsEngine.logResponse(
      session,
      requestName,
      ResponseTimings(startTimestamp = requestStartDate,
                      endTimestamp = requestEndDate),
      if (isEverythingSent) OK else KO,
      None,
      if (isEverythingSent) None else Some("Cannot send a request")
    )
  }

  private def logUnbuildableRequest(session: Session,
                                    requestName: String,
                                    errorMessage: String) = {
    statsEngine.reportUnbuildableRequest(session, requestName, errorMessage)
  }

  protected def logError(session: Session,
                         requestName: String,
                         errorMessage: String) = {
    statsEngine.logCrash(session, requestName, errorMessage)
  }

  protected def sendAll(payloads: List[Any]): List[Boolean] = {
    payloads match {
      case Nil            => List()
      case payload :: Nil => send(payload) :: sendAll(Nil)
      case payload :: xs  => sendMore(payload) :: sendAll(xs)
    }
  }

  private def sendMore(payload: Any): Boolean = {
    payload match {
      case _: Array[Byte]      => sock.sendMore(payload.asInstanceOf[Array[Byte]])
      case _: String           => sock.sendMore(payload.asInstanceOf[String])
      case _: java.lang.Number => sock.sendMore(payload.toString)
      case _ => {
        logger.error(
          s"Unsupported type of data provided: ${payload.getClass.getName}")
        false
      }
    }
  }

  private def send(payload: Any): Boolean = {
    payload match {
      case _: Array[Byte]      => sock.send(payload.asInstanceOf[Array[Byte]])
      case _: String           => sock.send(payload.asInstanceOf[String])
      case _: java.lang.Number => sock.send(payload.toString)
      case _ => {
        logger.error(
          s"Unsupported type of data provided: ${payload.getClass.getName}")
        false
      }
    }
  }

}
