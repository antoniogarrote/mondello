package mondello.electron

import knockout.{Ko, KoComponent}
import mondello.config.Settings
import mondello.electron.components.MondelloApp
import org.scalajs.dom

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExport
object Renderer {

  val fs = g.require("fs")

  @JSExport
  def main(): Unit = {
    Settings.loadSettings().map { (settings) =>
      println("*** Mondello running")
      println(settings)
      // Register components
      KoComponent(MondelloApp(settings))

      // Apply bindings
      Ko.applyBindings(MondelloApp, dom.document.getElementById("main"))

    }.onFailure {
      case e:Throwable =>
        val errorMessage = if(e.getMessage != null) { e.getMessage } else { e.toString }
        g.alert(s"Fatal error starting Mondello: $errorMessage")
        throw e
    }
  }
}
