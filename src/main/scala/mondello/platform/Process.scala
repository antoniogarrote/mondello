package mondello.platform

import mondello.config.Environment
import scala.concurrent.Future

abstract trait Process {
  def execute(command:String, commandArgs:Array[String]=Array[String]())(implicit environment: Environment):Future[Array[String]]
}
