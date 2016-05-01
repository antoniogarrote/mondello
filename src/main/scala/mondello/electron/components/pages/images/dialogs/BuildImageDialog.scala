package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}
import mondello.config.Log
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Images

import scala.scalajs.js
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scalatags.Text.all._
import scalatags.Text.attrs
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportAll
object BuildImageDialog extends KoComponent("build-image-dialog") {

  val dirname:KoObservable[String] = Ko.observable("")
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
            label("Dockerfile"),
            input(id:="build-image-path",`class`:="form-control", attrs.data.bind:="value: dirname"),
            button(id:="build-image-path-btn",`class`:="btn btn-form btn-default pull-right",
              attrs.data.bind:="click: openSelectDockerfile",
              span(`class`:="icon icon-drive")
            )
          ),
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

  def openSelectDockerfile() = {
    Log.trace("* Opening dockerfile")
    val options = js.Dictionary(
      "title" -> "Select Dockerfile to build",
      "properties" -> js.Array("openFile")
    )
    var filenames = g.require("remote").dialog.showOpenDialog(options).asInstanceOf[js.UndefOr[js.Array[String]] ]
    if(filenames.isDefined) {
      var filename = filenames.get(0)
      Log.trace(s"* Selected $filename")
      val sep = g.require("path").sep.asInstanceOf[String]
      val parts = filename.split(sep)
      val last = parts.last
      val projectDir = parts.dropRight(1).mkString(sep)
      Log.trace(s"* Last: $last")
      Log.trace(s"* Project dir: $projectDir")
      if(last == "Dockerfile") {
        dirname(projectDir)
      } else {
        g.alert("You must select a Dockerfile")
      }
    }
    false
  }

  def commitBuildImage() = {
    MondelloApp.showModal("Building Dockerfile")
    if(dirname != null && dirname() != "") {
      val f = Images.buildImage(dirname(), imageTag(), buildArgs(), removeContainers())
      f.onSuccess {
        case _ =>
          hide()
          MondelloApp.closeModal()
          Images.reloadImages()
      }
      f.onFailure {
        case e:Throwable =>
          g.alert(s"Error building image: ${e.getMessage}")
          MondelloApp.closeModal()
      }
    } else {
      g.alert("You must select a Dockerfile to build")
    }
  }
}
