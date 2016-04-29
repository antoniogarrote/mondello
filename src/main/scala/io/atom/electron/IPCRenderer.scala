package io.atom.electron

import scala.scalajs.js
import js.Dynamic.{global => g}

@js.native
trait IPCRenderer extends js.Object {
  def send[T](channel:String, message:T):js.Dynamic = js.native
  def sendSync[T,U](channel:String, message:T):U = js.native
}

object IPCRenderer {
  val ipc = g.require("electron").ipcRenderer.asInstanceOf[IPCRenderer]
  def send[T](channel:String, message:T):js.Dynamic = ipc.send(channel, message)
  def sendSync[T,U](channel:String, message:T):U = ipc.sendSync(channel, message)
}
