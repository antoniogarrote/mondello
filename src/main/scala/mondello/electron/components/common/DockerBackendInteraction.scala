package mondello.electron.components.common

import mondello.proxies.Docker
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

trait DockerBackendInteraction {

  def dockerTry(docker:Docker)(f: => Future[Boolean]): Future[Boolean] = {
    if(docker != null) {
      f
    } else {
      val f = Future(false)
      f.failed
      f
    }
  }
}
