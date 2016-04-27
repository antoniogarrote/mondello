package mondello.platform.js

import mondello.config.Environment

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

object Implicits {

  implicit object ConsoleProcess extends mondello.platform.Process {

    def commandString(cmdPath: String, command: String, commandArgs: Seq[String] = Array[String]()) = {
      val pathExport = g.process.env.PATH
      val commandArgsStr = commandArgs.mkString(" ")
      "export PATH=\"" + pathExport + "\"" + s"; $cmdPath $command $commandArgsStr"
    }

    def execute(command: String, commandArgs: Array[String] = Array[String]())(implicit environment: Environment): Future[Array[String]] = {
      val result = Promise[Array[String]]()

      val commandLine = commandString(environment.cmdPath, command, commandArgs)

      println(s"* Running command '$command'")
      var output = ""
      var err: String = null

      val child = g.require("child_process").exec(commandLine, { (stderr: js.Object, stdout: js.Object, stdin: js.Object) =>
        output += stdout
        if (stderr != null) {
          if (err != null) err += stderr else err = stderr.toString
        }
      })

      child.on("close", { (code: js.Object, signal: js.Object) =>
        if (err == null)
          result.success(output.split("\n"))
        else
          result.failure(new Exception(err))
      })

      result.future
    }
  }
}