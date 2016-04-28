package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}

import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._
import scalatags.Text.attrs

@ScalaJSDefined
object BuildImageDialog extends KoComponent {
  override val tagName: String = "build-image-dialog"
  val tag = KoComponent.mkTag(tagName)

  val buildArgs:KoObservable[String] = Ko.observable("")
  val imageTag:KoObservable[String] = Ko.observable("latest")
  val removeContainers:KoObservable[Boolean] = Ko.observable(true)

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="build-image-dialog", `class`:="floating-window", style:="display: none",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Build Image")
      ),
      div(`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Tag"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: imageTag")
          ),
          div(`class`:="form-group",
            label("Build Arguments"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: buildArgs")
          ),
          div(`class`:="checkbox",
            label(
              input(id:="build-image-rm", `type`:="checkbox",attrs.data.bind:="checked: removeContainers",
                "Remove intermediary containers"
              )
            )
          ),
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-cancel btn btn-form btn-default pull-right",
              attrs.data.bind:="click:cancelBuildImage",
              "Cancel"),
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-primary pull-right",
              attrs.data.bind:="click:commitBuildImage",
              "Ok")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // helper functions

  def show() = {
    buildArgs("")
    imageTag("latest")
    removeContainers(true)

    g.$("#build-image-dialog").show()
  }

  def hide() = {
    g.$("#build-image-dialog").hide()
  }

  // callbacks

  def cancelBuildImage() = {
    hide()
  }

  def commitBuildImage() = {
    hide()
  }
}
