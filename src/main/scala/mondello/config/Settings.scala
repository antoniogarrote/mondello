package mondello.config

import mondello.config.Log.trace
import mondello.electron.{Main, Messages, Renderer}
import mondello.electron.components.common.FileLoader

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Settings extends FileLoader {

  val defaultSettings = """{dockerHome: "", driversHome: "", compose: [],}"""

  val mondelloSettingsPath = Messages.mondelloSettingsPath
  var dockerHome:String = "/usr/local/bin"
  var driversHome:String = "/usr/local/bin"
  var compose = ListBuffer.empty[String]
  var error:String = null

  def loadSettings():Future[Settings.type] = {
    trace(s"* Reading settings from $mondelloSettingsPath")
    val p:Promise[Settings.type] = Promise()
    try {
      val fs = g.require("fs")
      loadFile(mondelloSettingsPath).map[Settings.type] { (data: String) =>
        var parsed = g.JSON.parse(data).asInstanceOf[js.Dictionary[js.Any]]
        trace("* Found settings")
        trace(data)
        dockerHome = parsed("dockerHome").asInstanceOf[String]
        driversHome = parsed("driversHome").asInstanceOf[String]
        val oldCompose = parsed("compose").asInstanceOf[js.Array[String]]
        var newCompose = ListBuffer[String]()
        for (project <- oldCompose) {
          newCompose += project
        }
        compose = newCompose
        p.success(this)
        this
      }.onFailure {
        case e: Throwable =>
          trace("* Settings file not found, returning defaults")
          this.error = e.toString
          p.success(this)
      }
    } catch {
      case e: Throwable =>
        trace("* Settings file not found, returning defaults")
        this.error = e.toString
        p.success(this)
    }

    p.future
  }

  def toEnv:Environment = {
    trace("** Generating environment")
    val dockerPath = (dockerHome ++ "/docker").replaceAllLiterally("//","/")
    trace(s"* Docker path $dockerPath")
    val dockerMachinePath = (dockerHome ++ "/docker-machine").replaceAllLiterally("//","/")
    trace(s"* Docker Machine path $dockerMachinePath")
    val dockerComposePath = (dockerHome ++ "/docker-compose").replaceAllLiterally("//","/")
    trace(s"* Docker Compose path $dockerComposePath")
    Environment("", dockerMachinePath, dockerPath, dockerComposePath)
  }

  def saveProject(file:String) = {
    trace("* Saving project")
    compose += file
    compose = compose.distinct
    persist()
  }

  def removeProject(file:String) = {
    trace(s"* Removing project $file")
    compose -= file
    persist()
  }

  def persist() = {
    trace("* Persisting config")
    val newState = js.Dictionary[js.Any]("dockerHome" -> dockerHome, "driversHome" -> driversHome)
    val projects = js.Array[String]()
    for(project <- compose) projects.push(project)
    newState.update("compose", projects)
    trace(this)
    saveFile(mondelloSettingsPath, g.JSON.stringify(newState).toString)
  }

  override def toString:String = {
    s"SETTINGS: home:$dockerHome, drivers:$driversHome, compose:$compose"
  }
}
