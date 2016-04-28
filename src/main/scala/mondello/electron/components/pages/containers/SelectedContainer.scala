package mondello.electron.components.pages.containers

import knockout.KoComponent

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}

@JSExportAll
object SelectedContainer extends KoComponent("selected-container") {

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    "<b>selected container</b>"
  }
}
