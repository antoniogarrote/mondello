package mondello.electron.components.pages.compose

import knockout.{KoComponent, KoObservable}
import mondello.models.Project

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

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

  def upDetached() = {
    println("* up project detached")
  }

  def upAttached() = {
    println("* up project attached")
  }
}
