package mondello.electron.components

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.pages.images.dialogs.{BuildImageDialog, LaunchConfigurationDialog, PullImageDialog}
import mondello.electron.components.pages.machines.dialogs.NewMachineDialog
import mondello.models.Machine

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object Toolbar extends KoComponent("mondello-toolbar") {


  var page:KoObservable[String] = null
  var selectedMachine:KoObservable[Machine] = null
  var newMachineDialog = NewMachineDialog

  nestedComponents += (
    "newMachineDialog" -> newMachineDialog,
    "PullImageDialog" -> PullImageDialog,
    "BuildImageDialog" -> BuildImageDialog
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    page = params("page").asInstanceOf[KoObservable[String]]
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[Machine]]
  }

  override def template: String = {
    header(`class`:="toolbar toolbar-header",
      h1(`class`:="title", "Mondello"),
      div(`class`:="toolbar-actions",
        commonButtons(),
        machinesToolbar(),
        imagesToolbar(),
        rightButtons()
      ),
      NewMachineDialog.tag(),
      PullImageDialog.tag(),
      BuildImageDialog.tag()
    ).toString()
  }

  // Template partials

  def commonButtons():Frag = {
    div(`class`:="btn-group",
      button(attrs.data.bind:="css: {active: page() == 'machines'}, click: function(){ selectPage('machines') }",
        `class`:="btn btn-default",
        span(`class`:="icon icon-drive"),
        raw("&nbsp; Machines")
      ),
      button(attrs.data.bind:="css: {active: page() === 'images','btn-disabled':(selectedMachine() && selectedMachine().state !== 'Running')},"+
        " click: function(){ selectPage('images') }",
        `class`:="btn btn-default",
        span(`class`:="icon icon-box"),
        raw("&nbsp; Images")
      ),
      button(attrs.data.bind:="css: {active: page() === 'containers','btn-disabled':(selectedMachine() && selectedMachine().state !== 'Running')}," +
        " click: function(){ selectPage('containers') }",
        `class`:="btn btn-default",
        span(`class`:="icon icon-rocket"),
        raw("&nbsp; Containers")
      ),
      button(attrs.data.bind:="css: {active: page() === 'projects','btn-disabled':(selectedMachine() && selectedMachine().state !== 'Running')}," +
        " click: function(){ selectPage('projects') }",
        `class`:="btn btn-default",
        span(`class`:="icon icon-pencil"),
        raw("&nbsp; Projects")
      )
    )
  }

  def machinesToolbar():Frag = {
    span(attrs.data.bind:="if: page()=='machines'",
      button(`class`:="btn btn-default",
        span(`class`:="icon icon-plus"), attrs.data.bind:="click: showNewMachine()",
        "New Machine"
      )
    )
  }

  def imagesToolbar():Frag = {
    span(attrs.data.bind:="if: page()=='images'",
      button(`class`:="btn btn-default",
        span(`class`:="icon icon-cloud"), attrs.data.bind:="click: pullImage",
        raw("&nbsp; Pull New")
      ),
      button(`class`:="btn btn-default",
        span(`class`:="icon icon-doc-text"), attrs.data.bind:="click: buildImage",
        raw("&nbsp; Build New")
      )
    )
  }

  def rightButtons():Frag = {
    span(
      button(`class`:="btn btn-default pull-right",
        span(`class`:="icon icon-cog"),
        raw("&nbsp; Settings")
      ),
      button(`class`:="btn btn-default pull-right",
        span(`class`:="icon icon-users"),
        raw("&nbsp; Credentials")
      )
    )
  }

  // callbacks

  def selectPage(page:String) = this.page(page)


  def showNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      println("* show new machine dialog")
      newMachineDialog.show()
      ()
    }
  }

  def pullImage() = {
    println("* Donwload image dialog")
    PullImageDialog.show()
  }

  def buildImage() = {
    println("* Build image dialog")
    BuildImageDialog.show()
  }
}
