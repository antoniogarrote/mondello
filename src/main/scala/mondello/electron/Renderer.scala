package mondello.electron

import knockout.{Ko, KoComponent}
import mondello.electron.components.MondelloApp
import org.scalajs.dom

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport


@JSExport
object Renderer {

  val fs = g.require("fs")

  @JSExport
  def main(): Unit = {
    println("*** Mondello running")

    // Register components
    KoComponent(MondelloApp())

    // Apply bindings
    Ko.applyBindings(MondelloApp, dom.document.getElementById("main"))

  }
}
