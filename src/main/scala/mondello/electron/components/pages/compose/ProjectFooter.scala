package mondello.electron.components.pages.compose

import knockout.{KoComponent, KoObservable}
import mondello.config.{Log, Settings}
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
          `class`:="btn btn-large btn-default",title:="Removes the project from the projects list",
          attrs.data.bind:="click: destroyProject, css: {'btn-disabled':!selectedProject()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Remove Project"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",title:="Builds, (re)creates, starts the selected services in the background",
          attrs.data.bind:="click: upDetached, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-up"),
          raw("&nbsp;"),
          "Up Detach."
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",title:="Builds, (re)creates, starts, and attaches to containers for the selected services",
          attrs.data.bind:="click: upAttached, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-publish"),
          raw("&nbsp;"),
          "Up Attach"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",title:="Stops the selected services",
          attrs.data.bind:="click: stop, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-stop"),
          raw("&nbsp;"),
          "Stop"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",title:="Stops all services and removes images and containers",
          attrs.data.bind:="click: down, css: {'btn-disabled': !selectedProject()}",
          span(`class`:="icon icon-down"),
          raw("&nbsp;"),
          "Down"
        ),
        raw("&nbsp;")
      )
    ).toString()
  }

  // callbacks

  def destroyProject() = {
    Log.trace("* destroy project")
    Settings.removeProject(selectedProject().file)
    Compose.reloadProjects()
  }

  def stop() = {
    Compose.stopSelectedServices()
  }

  def down() = {
    Compose.down()
  }

  def upDetached() = upInternal(detached = true)

  def upAttached() = upInternal(detached = false)

  def upInternal(detached:Boolean) = {
    Log.trace("* up project attached")
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
