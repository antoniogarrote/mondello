package mondello.electron.components.pages.containers

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Containers
import mondello.models.Container

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
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
          title:="Destroys the selected container",
          attrs.data.bind:="click: destroyContainer, css: {'btn-disabled':!selectedContainer()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Destroy Container"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Stops the selected container",
          attrs.data.bind:="click: stopContainer, css: {'btn-disabled': !selectedContainer() || !selectedContainer().running}",
          span(`class`:="icon icon-stop"),
          raw("&nbsp;"),
          "Stop"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Attaches to a running container",
          attrs.data.bind:="click: attachContainer, css: {'btn-disabled': !selectedContainer() || !selectedContainer().running}",
          span(`class`:="icon icon-flash"),
          raw("&nbsp;"),
          "Attach"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Starts a container in the background",
          attrs.data.bind:="click: startContainerDetached, css: {'btn-disabled': !selectedContainer() || selectedContainer().running}",
          span(`class`:="icon icon-play"),
          raw("&nbsp;"),
          "Start Detached"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Starts a container attached",
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
    if(selectedContainer() != null) {
      MondelloApp.showModal(s"Destroying container ${selectedContainer().names}")
      val f = Containers.destroyContainer(selectedContainer())
      f.onSuccess {
        case _ =>
          MondelloApp.closeModal()
          Containers.reloadContainers()
      }

      f.onFailure {
        case e:Throwable =>
          g.alert(s"Error destroying container ${e.getMessage}")
          MondelloApp.closeModal()
      }
    }
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
