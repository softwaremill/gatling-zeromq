package com.softwaremill.gatling.zeromq.support

import java.util.concurrent.{ExecutorService, Executors}

class Server(val socket: Socket) {

  val exec: ExecutorService = Executors.newFixedThreadPool(1)

  def run() = {
    exec.submit(socket)
  }

  def stop() = {
    exec.shutdown()
  }

}
