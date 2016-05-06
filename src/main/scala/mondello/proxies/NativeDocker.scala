package mondello.proxies

import mondello.config.{Environment, Log, Settings}
import mondello.models.Machine

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExportAll
import scalajs.js.Dynamic.{global => g}

@JSExportAll
class NativeDocker()(implicit ec:ExecutionContext, consoleProcess: mondello.platform.Process) {

  // General env for running random commands
  val env = Environment.defaultEnv.copy(cmdPath = "")

  /**
    * We check if the native docker is running by looking for the hypervisor process
    *
    * @return Future[Boolean]
    */
  def isRunning(): Future[Boolean] = consoleProcess.execute("ps aux | grep docker.osx.xhyve")(env).map { (lines:Array[String]) =>
    val noGrepLines = lines.filter(_.indexOf("grep") == -1)
      noGrepLines.length > 0
  }


  def isAvailable() = g.require("fs").existsSync(Settings.nativeDockerPath).asInstanceOf[Boolean]

  def start(listener:(String) => Unit = null):Future[Boolean] = {
    val running = Promise[Boolean]()
    consoleProcess.executeChild(s"${Settings.nativeDockerPath}/Contents/MacOS/Docker",Array(),{ (line:String) =>
      if(listener != null) listener(line)
      if(line == null) {
        if(!running.isCompleted) {
          running.failure(new Exception("Native process has been terminated"))
        }
      } else {
        if(line.indexOf("dockerState = running") != -1) {
          running.success(true)
        }
      }
    })(env)
    running.future
  }
/*
  def stop(): Future[Boolean] = {

  }
 */
}

object NativeDocker {
  def machineModel:Machine = new Machine("Native Docker","true","","Running","","",Map(),null)
}
