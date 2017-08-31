package com.softwaremill.gatling.zeromq.support

import org.zeromq.ZMQ

class Socket(val port: Int, val socketType: Int, val msgCount: Int = 1)
    extends Runnable {

  def run(): Unit = {
    val zmqCtx = ZMQ.context(1)
    val repSock = zmqCtx.socket(socketType)

    try {
      repSock.bind(s"tcp://localhost:$port")

      while ({
        !Thread.currentThread.isInterrupted
      }) {
        val request = (1 to msgCount)
          .map(_ => repSock.recvStr())
          .mkString(" ")
        repSock.send(s"$request ack".getBytes, 0)
      }
    } finally {
      repSock.close()
      zmqCtx.term()
    }
  }

}
