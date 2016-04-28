package mondello.platform

import mondello.config.Environment
import scala.concurrent.Future

trait Process {
  def execute(command:String, commandArgs:Array[String]=Array())(implicit environment: Environment):Future[Array[String]]
  def executeInteractive(command:String, commandArgs:Array[String]=Array())(implicit environment: Environment):Future[Array[String]]
}
