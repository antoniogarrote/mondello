package mondello.electron.components.common

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

trait DockerBackendInteraction {

  def dockerTry[T](docker:T)(f: => Future[Boolean]): Future[Boolean] = {
    if(docker != null) {
      f
    } else {
      val f = Future(false)
      f.failed
      f
    }
  }


  def dockerTry[T,U](docker:T, default:U)(f: => Future[U]): Future[U] = {
    if(docker != null) {
      f
    } else {
      val f = Future(default)
      f.failed
      f
    }
  }

}