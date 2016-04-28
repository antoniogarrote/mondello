package mondello.electron.components.pages.containers

import knockout.KoComponent

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}

@JSExportAll
object ContainerFooter extends KoComponent {
  override val tagName: String = "container-footer"
  val tag = KoComponent.mkTag(tagName)

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    "<b>container footer</b>"
  }
}
