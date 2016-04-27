package io.atom.electron

import scala.scalajs.js.annotation.JSName
import scalajs.js
import js.Dynamic.{global => g}

@js.native
trait Sender extends js.Object {
  def send[T](messageName:String, message:T) = js.native
}

@js.native
trait Event extends js.Object {
  def sender:Sender = js.native
}

@js.native
trait IPC extends js.Object {
  @JSName("on")
  def onMessage[T](messageName:String, cb:(Event, T)=>Unit) = js.native
  def reply[T](event:Event, messageName:String, message:T): Unit = {
    event.sender.send(messageName, message)
  }
}

object IPC {
  val ipc = g.require("ipc").asInstanceOf[js.Dynamic].ipcMain

  def apply():IPC = ipc.asInstanceOf[IPC]
}
