package mondello.electron.components.common

import mondello.config.Log

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


trait FileLoader {

  def loadFile(path:String):Future[String] = {
    val fs = g.require("fs")
    val p:Promise[String] = Promise()
    fs.readFile(path, { (err:js.UndefOr[Throwable],data:js.Any) =>
      if(err.isDefined && err.get != null) {
        p.failure(err.get)
      } else {
        p.success(data.toString)
      }
    })
    p.future
  }

  def loadYamlFile(path:String):Future[js.Dynamic] = {
    loadFile(path).map[js.Dynamic] {
      case (data:String) =>
        g.require("js-yaml").load(data)
    }
  }

  def saveFile(path:String, data:String):Unit = {
    val fs = g.require("fs")
    Log.trace(s"* Saving file $path")
    fs.writeFile(path, data, {(e:js.Dynamic)=> Log.trace(e)})
    ()
  }
}
