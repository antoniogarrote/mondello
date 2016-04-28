package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}

import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._
import scalatags.Text.attrs


@ScalaJSDefined
object PullImageDialog extends KoComponent{
  override val tagName: String = "pull-image-dialog"
  val tag = KoComponent.mkTag(tagName)

  val imageName:KoObservable[String] = Ko.observable("")
  val imageTag:KoObservable[String] = Ko.observable("latest")

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="pull-image-dialog", `class`:="floating-window", style:="display: none",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Pull Image")
      ),
      div(`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Image Name"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: imageName")
          ),
          div(`class`:="form-group",
            label("Tag"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: imageTag")
          ),
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-cancel btn btn-form btn-default pull-right",
              attrs.data.bind:="click:cancelPullImage",
              "Cancel"),
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-primary pull-right",
              attrs.data.bind:="click:commitPullImage",
              "Ok")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // helper functions

  def show() = {
    imageName("")
    imageTag("latest")

    g.$("#pull-image-dialog").show()
  }

  def hide() = {
    g.$("#pull-image-dialog").hide()
  }

  // callbacks

  def cancelPullImage() = {
    hide()
  }

  def commitPullImage() = {
    hide()
  }
}
