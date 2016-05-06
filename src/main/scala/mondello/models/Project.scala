package mondello.models

import mondello.electron.components.common.FileLoader

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.JSConverters
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
class Service(val id:String, val service:js.Dynamic) {

  def extract[T](p: () =>js.Dynamic, df: () => T):T = {
    val d = p()
    if(d.asInstanceOf[js.UndefOr[T]].isEmpty) {
      df()
    } else {
      d.asInstanceOf[T]
    }
  }

  protected def extractString(p: () => js.Dynamic) = extract[String](p, () => "")
  protected def extractArray(p: () => js.Dynamic) = extract[js.Array[String]](p, () => js.Array[String]())
  protected def extractDictionary(p: () => js.Dynamic) = extract[js.Dictionary[String]](p, () => js.Dictionary[String]())
  protected def extractArrayMaps(p: () => js.Dynamic) = extract[js.Array[js.Dictionary[String]]](p, () => js.Array[js.Dictionary[String]]())

  val from = extractString(() => service.image ) ++ extractString(() =>service.build)

  val source = if(extractString(() => service.build) == "") { "image" } else { "build" }

  val portsCount = extractArray(()=> service.ports ).length

  val ports:js.Array[Map[String,String]] = extractArray(() => service.ports).map {
    (portLine) =>
      val parts = portLine.split(":")
      Map[String,String]("from" -> parts(0), "to" -> parts(1))
  }

  val portsJS:js.Array[js.Dictionary[String]] = {
    ports.map { (m) => JSConverters.JSRichGenMap[String](m).toJSDictionary }
  }

  val links = extractArray(() => service.links)

  val externalLinks = extractArray(() => service.external_links)

  val linksCount = links.length

  val volumes:js.Array[Map[String,String]] = extractArray(() => service.volumes).map {
    (volume) =>
      val parts = volume.split(":")
      Map[String,String]("host" -> parts(0), "container" -> parts(1))
  }

  val volumesJS:js.Array[js.Dictionary[String]] = {
    volumes.map { (m) => JSConverters.JSRichGenMap[String](m).toJSDictionary }
  }
  val volumesCount = volumes.length

  val environment:js.Array[Map[String,String]] = try {
    extractArray(() => service.environment).map {
      (env) =>
        val parts = env.split("=")
        Map[String,String]("key" -> parts(0), "val" -> parts(1))
    }

  } catch {
    case e1:Throwable => try {
      val newDictionary = js.Array[Map[String,String]]()
      val oldDictionary = extractDictionary(()=> service.environment )
      for(key <- oldDictionary.keys) {
        newDictionary.push(Map("key" -> key, "val" -> oldDictionary(key)))
      }
      newDictionary
    } catch {
      case e2:Throwable =>
        js.Array[Map[String,String]]()
    }
  }

  val environmentJS:js.Array[js.Dictionary[String]] = {
    environment.map { (m) => JSConverters.JSRichGenMap[String](m).toJSDictionary }
  }

  val envsCount = environment.length

  var selected = false
}

@JSExportAll
class Project(val file:String, val services:Array[Service]) {
  val filename = g.require("path").basename(file).asInstanceOf[String]
  val dirname = g.require("path").dirname(file).asInstanceOf[String]
  val localDir = s"/${dirname.split(g.require("path").sep.asInstanceOf[String]).last}"
  val servicesCount = services.length

  def servicesJS:js.Array[Service] = js.JSConverters.array2JSRichGenTrav(services).toJSArray
}

object Project extends FileLoader {
  def load(file:String):Future[Project] = {
    loadYamlFile(file).map[Project] { (data) =>
      val servicesDictionary = data.asInstanceOf[js.Dictionary[js.Dynamic]]
      val services = servicesDictionary.keys.map { (id) =>
        new Service(id, servicesDictionary(id))
      }
      new Project(file, services.toArray)
    }
  }
}