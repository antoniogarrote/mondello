package mondello.electron.components.pages.compose

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.{Compose, Containers}
import mondello.models.Project

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


@JSExportAll
object ProjectFooter extends KoComponent("project-footer") {

  var selectedProject:KoObservable[Project] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedProject = params("selectedProject").asInstanceOf[KoObservable[Project]]
  }

  override def template: String = {
    footer(`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: destroyProject, css: {'btn-disabled':!selectedProject()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Remove Project"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: upDetached, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-up"),
          raw("&nbsp;"),
          "Up Detach."
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: upAttached, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-publish"),
          raw("&nbsp;"),
          "Up Attach"
        ),
        raw("&nbsp;")
      )
    ).toString()
  }

  // callbacks

  def destroyProject() = {
    println("* destroy project")
  }

  def upDetached() = upInternal(detached = true)

  def upAttached() = upInternal(detached = false)

  def upInternal(detached:Boolean) = {
    println("* up project attached")
    MondelloApp.showModal("Starting selected services detached")
    val f = Compose.upSelectedServices(detached)
    f.onSuccess {
      case _:Boolean =>
        MondelloApp.closeModal()
        MondelloApp.reloadSelectedMachine()
    }

    f.onFailure {
      case e:Throwable =>
        MondelloApp.closeModal()
        g.alert(s"Error starting services: ${e.getMessage}")
    }
  }
}
