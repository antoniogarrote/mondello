package mondello.electron.components.pages.machines

import knockout.{KoComponent, KoObservable}
import mondello.electron.Renderer
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Machines

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs


@ScalaJSDefined
class MachineFooter extends KoComponent {
  override val tagName: String = MachineFooter.tagName

  var selectedMachine:KoObservable[mondello.models.Machine] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[mondello.models.Machine]]
  }

  override def template: String = {
    footer(`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: destroyMachine(), css: {'btn-disabled':!selectedMachine()}",
          span(`class`:="icon icon-trash"),
          "Destroy Machine"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: stopMachine(), css: {'btn-disabled': (selectedMachine() == null || selectedMachine().state == 'Stopped')}",
          span(`class`:="icon icon-stop"),
          "Stop"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: startMachine(), css: {'btn-disabled': (selectedMachine() == null || selectedMachine().state == 'Running')}",
          span(`class`:="icon icon-play"),
          "Start"
        )
      )
    ).toString()
  }

  // Callbacks

  def destroyMachine():js.Function1[js.Any,Unit] = {
    (event:js.Any) => {
      println("** Destroying machine clicked")
      MondelloApp.showModal(s"Destroying Docker machine ${this.selectedMachine().name}")
      Machines.removeMachine(this.selectedMachine(), (_) => MondelloApp.closeModal())
      ()
    }
  }

  def stopMachine():js.Function1[js.Any,Unit] = {
    (event:js.Any) => {
      println("* Stop machine clicked")
      MondelloApp.showModal(s"Stopping Docker machine '${this.selectedMachine().name}'")
      println(s"* Stopping machine ${this.selectedMachine().name}")
      Machines.stopMachine(this.selectedMachine(), (_) => MondelloApp.closeModal())
      ()
    }
  }

  def startMachine():js.Function1[js.Any,Unit] = (event:js.Any) => {
    println("* Start machine clicked")
    MondelloApp.showModal(s"Starting Docker machine '${this.selectedMachine().name}")
    println(s"* Starting machine ${this.selectedMachine().name}")
    Machines.startMachine(this.selectedMachine(), (_) => MondelloApp.closeModal())
    ()
  }
}

object MachineFooter {
  val tagName = "machine-footer"
  val tag = KoComponent.mkTag(tagName)
}
