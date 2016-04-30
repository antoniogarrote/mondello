package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Images

import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scalatags.Text.all._
import scalatags.Text.attrs

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


@JSExportAll
object
PullImageDialog extends KoComponent("pull-image-dialog") {

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
    if(imageName() != "") {
      MondelloApp.showModal(s"Pulling image $imageName():$imageTag()")
      val f = Images.pullImage(imageName(), imageTag())
      f.onSuccess {
        case _ =>
          MondelloApp.closeModal()
          hide()
          Images.reloadImages()
      }

      f.onFailure {
        case e:Throwable =>
          g.alert(s"Error pulling image: ${e.getMessage}")
      }
    } else {
      g.alert("Please, provide a name for the image")
    }
  }
}
