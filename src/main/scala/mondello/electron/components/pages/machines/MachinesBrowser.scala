package mondello.electron.components.pages.machines

import knockout.{KoComponent, KoObservable, KoObservableArray}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._
import scalatags.Text.tags2._
import scalatags.Text.{TypedTag, attrs}

@ScalaJSDefined
class MachinesBrowser extends KoComponent {

  override val tagName: String = MachinesBrowser.tagName

  var loadingMachines:KoObservable[Boolean] = null
  var machines:KoObservableArray[SelectedMachine] = null
  var selectedMachine:KoObservable[SelectedMachine] = null

  override def viewModel(params: js.Dictionary[js.Any]): Unit = {
    machines = params("machines").asInstanceOf[KoObservableArray[SelectedMachine]]
    loadingMachines = params("loadingMachines").asInstanceOf[KoObservable[Boolean]]
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[SelectedMachine]]
  }

  def template: String = {
    nav(`class`:="nav-group",
      h5(`class`:="nav-group-title", "Machines"),
      // Not machines
      span(`class`:="nav-group-item",
           attrs.data.bind:="if: loadingMachines",
        div(`class`:="media-body",
          div(`class`:="spinner",
            div(`class`:="rect1"),
            div(`class`:="rect2"),
            div(`class`:="rect3"),
            div(`class`:="rect4"),
            div(`class`:="rect5")
          ))
      ),
      // Machines
      span(attrs.data.bind:="ifnot: loadingMachines",
        span(attrs.data.bind:="foreach: machines",
          span(`class`:="nav-group-item",
            attrs.data.bind:="click: $parent.selectMachine(),"++
              "css: {active: ($parent.selectedMachine() && $parent.selectedMachine().name == name)}",
            span(attrs.data.bind:="visible: state == 'Running'",
              `class`:="icon icon-record",attrs.style:="color:#34c84a"),
            span(attrs.data.bind:="visible: state == 'Saved'",
              `class`:="icon icon-record",attrs.style:="color:#fdbc40"),
            span(attrs.data.bind:="visible: state == 'Stopped'",
              `class`:="icon icon-record",attrs.style:="color:#fc605b"),
            span(attrs.data.bind:="visible: state == 'Error'",
              `class`:="icon icon-record",attrs.style:="color:color:#000000"),
            span(`class`:="icon icon-drive"),
            span(attrs.data.bind:="text: name")
          )
        )
      )
    ).toString()
  }

  // Callbacks

  def selectMachine():js.Function2[SelectedMachine,js.Any,Unit] = {
    (machine:SelectedMachine, event:js.Any) => this.selectedMachine(machine)
  }
}

object MachinesBrowser {
  val tagName:String = "machines-browser"
  def tag:TypedTag[String] = KoComponent.mkTag(tagName)
}