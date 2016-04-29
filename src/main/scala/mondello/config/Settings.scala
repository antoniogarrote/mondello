package mondello.config

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

object Settings {

  val defaultSettings = """{dockerHome: "", driversHome: "", compose: [],}"""

  var dockerHome:String = "/usr/local/bin"
  var driversHome:String = "/usr/local/bin"
  var compose = ListBuffer.empty[String]
  var error:String = null

  def loadSettings():Future[Settings.type] = {
    println("* Reading settings")
    val p:Promise[Settings.type] = Promise()
    try {
      val app = g.require("app")
      val path = g.require("path")
      val fs = g.require("fs")
      val mondelloSettingsPath = path.join(app.getPath("appData"), "mondello.json")

      fs.readFile(mondelloSettingsPath, { (err:js.UndefOr[js.Any],data:js.Any) =>
        if(err.isDefined) {
          error = err.get.toString
        } else {
          var parsed = g.JSON.parse(data).asInstanceOf[js.Dictionary[js.Any]]
          println("* Found settings")
          println(parsed)
          dockerHome = parsed("dockerHome").asInstanceOf[String]
          driversHome = parsed("driversHome").asInstanceOf[String]
          val oldCompose = parsed("compose").asInstanceOf[js.Array[String]]
          var newCompose = ListBuffer[String]()
          for(project <- oldCompose) {
            newCompose += project
          }
          compose = newCompose
        }
        p.success(this)
      })
    } catch {
      case e:Throwable =>
        println("* Settings file not found, returning defaults")
        this.error = e.toString
        p.success(this)
    }
    p.future
  }

  def toEnv:Environment = {
    println("** Generating environment")
    val dockerPath = (dockerHome ++ "/docker").replaceAllLiterally("//","/")
    println(s"* Docker path $dockerPath")
    val dockerMachinePath = (dockerHome ++ "/docker-machine").replaceAllLiterally("//","/")
    println(s"* Docker Machine path $dockerMachinePath")
    val dockerComposePath = (dockerHome ++ "/docker-compose").replaceAllLiterally("//","/")
    println(s"* Docker Compose path $dockerComposePath")
    Environment("", dockerMachinePath, dockerPath, dockerComposePath)
  }

  override def toString:String = {
    s"SETTINGS: home:$dockerHome, drivers:$driversHome, compose:$compose"
  }
}
