package mondello.electron.components.pages.machines.dialogs

import knockout.{Ko, KoComponent}
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Machines

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs


@ScalaJSDefined
class NewMachineDialog extends KoComponent {
  override val tagName: String = NewMachineDialog.tagName

  var machineName = Ko.observable[String](null)
  var machineDriver = Ko.observable[String]("virtualbox")
  var machineLabels = Ko.observable[String]("")
  var machineEnvs = Ko.observable[String]("")

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="new-machine-dialog", `class`:="floating-window", style:="display: none",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "New Machine")
      ),
      div(`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Name"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: machineName")
          ),
          div(`class`:="form-group",
            label("Driver"),
            input(id:="new-machine-driver", `class`:="form-control",attrs.data.bind:="value: machineDriver", value:="virtualbox")
          ),
          div(`class`:="form-group",
            label("Labels"),
            input(id:="new-machine-label", `class`:="form-control",attrs.data.bind:="value: machineLabels")
          ),
          div(`class`:="form-group",
            label("Environment"),
            input(id:="new-machine-env", `class`:="form-control",attrs.data.bind:="value: machineEnvs")
          ),
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-cancel btn btn-form btn-default pull-right",
              attrs.data.bind:="click:cancelNewMachine()",
              "Cancel"),
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-primary pull-right",
              attrs.data.bind:="click:createNewMachine()",
              "Ok")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // Utility functions

  def show() = {
    machineName(null)
    machineDriver("virtualbox")
    machineLabels("")
    machineEnvs("")

    g.$("#new-machine-dialog").show()
  }

  def hide() = {
    g.$("#new-machine-dialog").hide()
  }

  // callbacks

  def cancelNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      hide()
      ()
    }
  }

  def createNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      if(machineName() != null && machineDriver() != null) {
        val oldMachineName = machineName()
        val oldMachineDriver = machineDriver()
        val oldMachineLabels = machineLabels()
        val oldMachineEnvs = machineEnvs()
        val labels = oldMachineLabels.split(",").filter(_ != "").toList
        val envs = oldMachineEnvs.split(",").filter(_ != "").toList

        MondelloApp.showModal(s"Creating new machine '${machineName()}'")
        hide()
        Machines.createMachine(machineName(),machineDriver(), labels, envs, {
          case true  => MondelloApp.closeModal()
          case false =>
            MondelloApp.closeModal()
            show()
            machineName(oldMachineName)
            machineDriver(oldMachineDriver)
            machineLabels(oldMachineLabels)
            machineEnvs(oldMachineEnvs)
        })
      } else {
        g.alert("Name and driver must be provided when creating a new machine")
      }
      ()
    }
  }

}

object NewMachineDialog {
  val tagName = "new-machine-dialog"
  val tag = KoComponent.mkTag(tagName)
}
