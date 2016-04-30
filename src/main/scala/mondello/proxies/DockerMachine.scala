package mondello.proxies

import mondello.config.Environment
import mondello.models.Machine

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalajs.js

class DockerMachine(env: Environment)(implicit ec:ExecutionContext, consoleProcess: mondello.platform.Process){

  implicit val currentEnv = env.copy(cmdPath = env.dockerMachinePath)

  def all: Future[List[Machine]] = {
    consoleProcess.execute("ls").map { (lines:Array[String]) =>
      lines.drop(1).map(parseMachineLine(_))
    }.flatMap[List[Machine]] { (machines:Array[Machine]) =>
      val completeMachines = machines.map { (machine) =>
        for {
          env     <- fetchEnv(machine)
          inspect <- fetchInspect(machine)
        } yield machine match {
          case Machine(name, active, driver, state, url, swarm, _, _) =>
            new Machine(name, active, driver, state, url, swarm, env, inspect)
        }
      }
      Future.sequence(completeMachines.toList)
    }
  }

  def start(name:String): Future[Boolean] = consoleProcess.execute("start", Array(name)).map((_) => true)

  def stop(name:String): Future[Boolean] = consoleProcess.execute("stop", Array(name)).map((_) => true)

  def newMachine(name:String, driver:String, labels:List[String], env:List[String]): Future[Boolean] = {
    val driverCmd = s"--driver=$driver"
    val labelsCmd = labels.map((label) => s"--engine-label=$label")
    val envCmd = env.map((e) => s"--engine-env=$e")
    val args = List(name, driverCmd) ++ labelsCmd ++ envCmd
    consoleProcess.execute("create", (List(name, driverCmd) ++ labelsCmd ++ envCmd).toArray).map { case (_) => true }
  }

  def remove(name:String): Future[Boolean] = consoleProcess.execute("rm", Array("-f", name)).map((_) => true)

  protected def parseMachineLine(line:String):Machine = {
    line.split("\\s+") match {
      case Array(name, active, driver, state) =>
        new Machine(name, active, driver, state, null, null, null, null)
      case Array(name, active, driver, state, url) =>
        new Machine(name, active, driver, state, url, null, null, null)
      case Array(name, active, driver, state, url, swarm, _*) =>
        new Machine(name, active, driver, state, url, swarm, null, null)
      case parts => throw new Exception(s"Unknown docker-machine line format: size ${parts.length}")
    }
  }

  protected def fetchEnv(machine:Machine): Future[Map[String,String]] = {
    machine match {
      case Machine(name, active, driver, "Running", url, swarm, _, _) =>
        consoleProcess.execute("env", Array[String](name)).map(processEnv(_))
      case _ => Future.successful[Map[String,String]](null)
    }
  }

  protected def fetchInspect(machine:Machine): Future[js.Object] = {
    machine match {
      case Machine(name, active, driver, state, url, swarm, _, _) =>
        consoleProcess.execute("inspect", Array[String](name)).map(processInspect(_))
    }
  }

  protected def processEnv(lines: Array[String]): Map[String,String] = {
    val exportRegex = "export\\s+(.+)=\"(.+)\""
    lines.map { (line) =>
      if(line.matches(exportRegex)) {
        val groups = exportRegex.r.findAllIn(line).matchData.next
        (groups.group(1), groups.group(2))
      } else null
    }.foldLeft(Map[String,String]()) {
      case (acc, null)         => acc
      case (acc, (key, value)) => acc + (key -> value)
    }
  }

  protected def processInspect(strings: Array[String]): js.Object = {
    val json = strings.reduce(_ + _)
    js.JSON.parse(json).asInstanceOf[js.Object]
  }
}