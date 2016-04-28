package mondello.platform.js

import mondello.config.Environment

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.{Any, JSConverters}
import scala.scalajs.js.Dynamic.{global => g, newInstance => jsnew}


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

    override def executeInteractive(command: String, commandArgs: Array[String])(implicit environment: Environment): Future[Array[String]] = {
      val result = Promise[Array[String]]()

      val tmp = g.require("temporary")
      val fs = g.require("fs")
      val file = jsnew(tmp.File)()
      val oldPath = file.path
      val path = file.path + ".command"
      fs.renameSync(oldPath, path)
      fs.chmod(path,"777")

      val commandLine = commandString(environment.cmdPath, command, commandArgs)
      println(s"* Running command interactively '$command' through file: $path")
      fs.writeFile(path, commandLine, { (err:js.Dynamic) =>
        if(err != null) {
          result.failure(new Throwable(err.toString))
        } else {
          var output = ""
          var err: String = null
          val child = g.require("child_process").exec(s"open $path", { (stderr: js.Object, stdout: js.Object, stdin: js.Object) =>
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
        }
      })

      result.future
    }

    override def executeChild(command: String, commandArgs: Array[String], cb: (String) => Unit)(implicit environment: Environment): Any = {
      val spawnArgsArray = (List(command) ++ commandArgs).toArray
      val spawnArgs:js.Array[String] =  (new JSConverters.JSRichGenTraversableOnce[String](spawnArgsArray)).toJSArray
      val child = g.require("child_process").spawn(environment.cmdPath, spawnArgs, js.Dictionary("shell" -> true))
      val jscb:scalajs.js.Function1[js.Any,Unit] = {(data:js.Any) => cb(data.toString) }
      val closecb:scalajs.js.Function1[js.Any,Unit] = {(_:js.Any) => cb(null) }

      child.stdout.on("data", jscb);
      child.stderr.on("data", jscb);
      child.on("close", closecb);

      child
    }
  }
}