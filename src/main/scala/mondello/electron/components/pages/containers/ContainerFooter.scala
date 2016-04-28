package mondello.electron.components.pages.containers

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.pages.Containers
import mondello.models.Container

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ContainerFooter extends KoComponent("container-footer") {

  var selectedContainer:KoObservable[Container] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedContainer = params("selectedContainer").asInstanceOf[KoObservable[Container]]
  }

  override def template: String = {
    footer(`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: destroyContainer, css: {'btn-disabled':!selectedContainer()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Destroy Container"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: stopContainer, css: {'btn-disabled': !selectedContainer() || !selectedContainer().running}",
          span(`class`:="icon icon-stop"),
          raw("&nbsp;"),
          "Stop"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: attachContainer, css: {'btn-disabled': !selectedContainer() || !selectedContainer().running}",
          span(`class`:="icon icon-flash"),
          raw("&nbsp;"),
          "Attach"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: startContainerDetached, css: {'btn-disabled': !selectedContainer() || selectedContainer().running}",
          span(`class`:="icon icon-play"),
          raw("&nbsp;"),
          "Start Detached"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: startContainerInteractive, css: {'btn-disabled': !selectedContainer() || selectedContainer().running}",
          span(`class`:="icon icon-monitor"),
          raw("&nbsp;"),
          "Start Interactive"
        )
      )
    ).toString()
  }

  // Callbacks

  def destroyContainer() = {
    println("* Destroy container")
  }

  def stopContainer() = {
    println("* Stop Container")
    Containers.stopContainer(ContainerFooter.selectedContainer())
  }

  def attachContainer() = {
    println("* Attach Container")
  }

  def startContainerDetached() = {
    println("* Start Container Detached")
    Containers.startContainer(ContainerFooter.selectedContainer())
  }

  def startContainerInteractive() = {
    println("* Start Container Interactive")
    Containers.startContainerInteractive(ContainerFooter.selectedContainer())
  }
}
