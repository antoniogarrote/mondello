package mondello.electron.components.pages.machines

import knockout.{Ko, KoComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs


@ScalaJSDefined
class NewMachineDialog extends KoComponent {
  override val tagName: String = NewMachineDialog.tagName

  var machineName = Ko.observable[String](null)
  var machineDriver = Ko.observable[String](null)
  var machineLabels = Ko.observable[String](null)
  var machineEnvs = Ko.observable[String](null)

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
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: machineName()")
          ),
          div(`class`:="form-group",
            label("Driver"),
            input(id:="new-machine-driver", `class`:="form-control",attrs.data.bind:="value: machineDriver()", value:="virtualbox")
          ),
          div(`class`:="form-group",
            label("Labels"),
            input(id:="new-machine-label", `class`:="form-control",attrs.data.bind:="value: machineLabels()")
          ),
          div(`class`:="form-group",
            label("Environment"),
            input(id:="new-machine-env", `class`:="form-control",attrs.data.bind:="value: machineEnvs()")
          ),
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-cancel btn btn-form btn-default pull-right",
              attrs.data.bind:="click:cancelNewMachine()"),
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-primary pull-right",
              attrs.data.bind:="click:createNewMachine()")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // callbacks

  def cancelNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      println("* Cancel new machine")
    }
  }

  def createNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      println("* Create new machine")
    }
  }

}

object NewMachineDialog {
  val tagName = "new-machine-dialog"
  val tag = KoComponent.mkTag(tagName)
}
