package mondello.platform

import mondello.config.Environment
import scala.concurrent.Future
import scalajs.js.Any

trait Process {
  def execute(command:String, commandArgs:Array[String]=Array(),neverFail:Boolean=false)(implicit environment: Environment):Future[Array[String]]
  def executeInteractive(command:String, commandArgs:Array[String]=Array())(implicit environment: Environment):Future[Array[String]]
  def executeChild(command:String, commandArgs:Array[String]=Array(),cb:(String) => Unit)(implicit environment: Environment):Any
}
