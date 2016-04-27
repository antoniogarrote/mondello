import mondello.config.Environment

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.JSApp
import mondello.platform.js.Implicits.ConsoleProcess
import mondello.proxies.{Docker, DockerMachine}
/*
@scala.scalajs.js.annotation.JSExport
object HelloApp extends JSApp {
  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {
    val dm = new DockerMachine(environment)
    dm.all.onComplete( (machines) => println(s"*** GOT RESULT $machines"))

    val d = new Docker("default", environment)
    d.images.onComplete( (images) => println(s"*** GOT IMAGES RESULT $images"))

    d.containers.onComplete( (containers) => println(s"*** GOT CONTAINERS RESULT $containers"))
  }

  def environment: Environment = Environment.defaultEnv.copy(
    dockerMachinePath = "/usr/local/bin/docker-machine",
    dockerPath = "/usr/local/bin/docker")
}
*/