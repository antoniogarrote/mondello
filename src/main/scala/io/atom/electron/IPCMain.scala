package io.atom.electron

import scala.scalajs.js.annotation.JSName
import scalajs.js
import js.Dynamic.{global => g}

@js.native
trait Sender extends js.Object {
  def send[T](channel:String, message:T) = js.native
}

@js.native
trait Event[U] extends js.Object {
  def sender:Sender = js.native
  var returnValue:U = js.native
}

object IPCMain {
  val ipc = g.require("electron").ipcMain

  def onMessage[T,U](channel:String, cb:(Event[U], T)=>Unit) = {
    val wcb:js.Function = {(e:js.Dynamic,m:js.Dynamic) => cb(e.asInstanceOf[Event[U]],m.asInstanceOf[T]) }
    ipc.on(channel, wcb)
  }
  def reply[T,U](event:Event[U], messageName:String, message:T): Unit = {
    event.sender.send(messageName, message)
  }
}
