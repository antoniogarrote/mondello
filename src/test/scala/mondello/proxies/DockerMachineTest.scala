package mondello.proxies

import mondello.config.Environment
import mondello.models.Machine
import utest._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.Any


object DockerMachineTest extends TestSuite {

  implicit object TestConsoleProcess extends mondello.platform.Process {
    override def execute(command: String, commandArgs: Array[String])(implicit environment: Environment): Future[Array[String]] = {
      val result = Promise[Array[String]]()
      val text = command match {
        case "ls" => """NAME          ACTIVE   DRIVER       STATE     URL                         SWARM
                       |integration   -        virtualbox   Running   tcp://192.168.99.100:2376
                       |tests         -        virtualbox   Stopped""".stripMargin
        case "env" => """export DOCKER_TLS_VERIFY="1"
                        |export DOCKER_HOST="tcp://192.168.99.100:2376"
                        |export DOCKER_CERT_PATH="/Users/antonio/.docker/machine/machines/integration"
                        |export DOCKER_MACHINE_NAME="integration"
                        |# Run this command to configure your shell:
                        |# eval "$(docker-machine env integration)""".stripMargin
        case "inspect" => "{\"a\":\"b\"}"
      }
      result.success(text.split("\n")).future
    }

    override def executeInteractive(command: String, commandArgs: Array[String])(implicit environment: Environment): Future[Array[String]] = ???

    override def executeChild(command: String, commandArgs: Array[String], cb: (String) => Unit)(implicit environment: Environment): Any = ???
  }

  val tests = this {
    'processEnv {
      val dockerMachine = new DockerMachine(Environment.defaultEnv.copy(cmdPath = "testCmdPath"))
      val result = dockerMachine.all
      val testResult = Promise[Boolean]()

      result.onComplete { (machines) =>
        try {
          assert(machines.isSuccess)
          machines.get.head match {
            case Machine(name, _, _, _, _, _, env, _) =>
              assert(name == "integration")
              assert(env("DOCKER_TLS_VERIFY") == "1")
          }

          machines.get(1) match {
            case Machine(name, _, _, _, _, _, _, _) => assert(name == "tests")
          }

          testResult.success(true)
        } catch {
          case e:Throwable => testResult.failure(e)
        }
      }

      testResult.future
    }
  }
}
