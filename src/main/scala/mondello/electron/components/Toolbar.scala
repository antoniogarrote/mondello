package mondello.electron.components

import knockout.{KoComponent, KoObservable}
import mondello.config.{Log, Settings}
import mondello.electron.components.common.FileLoader
import mondello.electron.components.pages.Compose
import mondello.electron.components.pages.images.dialogs.{BuildImageDialog, PullImageDialog, SearchImageDialog}
import mondello.electron.components.pages.machines.dialogs.NewMachineDialog
import mondello.models.{Machine, Project}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs


@JSExportAll
object Toolbar extends KoComponent("mondello-toolbar") with FileLoader {


  var page:KoObservable[String] = null
  var selectedMachine:KoObservable[Machine] = null
  var newMachineDialog = NewMachineDialog
  var displayContainerLogs:KoObservable[Boolean] = null
  var showLogin:KoObservable[Boolean] = null
  var showSettings:KoObservable[Boolean] = null
  var nativeDockerAvailable:KoObservable[Boolean] = null
  var nativeDockerRunning:KoObservable[Boolean] = null


  nestedComponents += (
    "newMachineDialog" -> newMachineDialog,
    "PullImageDialog" -> PullImageDialog,
    "BuildImageDialog" -> BuildImageDialog,
    "SearchImageDialog" -> SearchImageDialog
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    page = params("page").asInstanceOf[KoObservable[String]]
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[Machine]]
    displayContainerLogs = params("displayContainerLogs").asInstanceOf[KoObservable[Boolean]]
    showLogin = params("showLogin").asInstanceOf[KoObservable[Boolean]]
    showSettings = params("showSettings").asInstanceOf[KoObservable[Boolean]]
    nativeDockerAvailable = params("nativeDockerAvailable").asInstanceOf[KoObservable[Boolean]]
    nativeDockerRunning = params("nativeDockerRunning").asInstanceOf[KoObservable[Boolean]]
  }

  override def template: String = {
    header(`class`:="toolbar toolbar-header",
      h1(`class`:="title", "Mondello"),
      div(`class`:="toolbar-actions",
        span(`class`:="span-toolbar-actions",
          button(`class`:="btn btn-default",title:="Reload Docker information",
            span(`class`:="icon icon-arrows-ccw"), attrs.data.bind:="click: reload",
            raw("&nbsp; Reload")
          )
        ),
        commonButtons(),
        machinesToolbar(),
        imagesToolbar(),
        containersToolbar(),
        composeToolbar(),
        rightButtons()
      ),
      NewMachineDialog.tag(),
      PullImageDialog.tag(),
      BuildImageDialog.tag(),
      SearchImageDialog.tag()
    ).toString()
  }

  // Template partials

  def commonButtons():Frag = {
    div(`class`:="btn-group",
      button(attrs.data.bind:="css: {active: page() == 'machines'}, click: function(){ selectPage('machines') }",
        title:="Docker machines management",
        `class`:="btn btn-default",
        span(`class`:="icon icon-drive"),
        raw("&nbsp; Machines")
      ),
      button(attrs.data.bind:="css: {active: page() === 'images','btn-disabled':(!selectedMachine() || selectedMachine().state !== 'Running')},"+
        " click: function(){ selectPage('images') }",
        title:="Docker images management",
        `class`:="btn btn-default",
        span(`class`:="icon icon-box"),
        raw("&nbsp; Images")
      ),
      button(attrs.data.bind:="css: {active: page() === 'containers','btn-disabled':(!selectedMachine() || selectedMachine().state !== 'Running')}," +
        " click: function(){ selectPage('containers') }",
        title:="Docker containers management",
        `class`:="btn btn-default",
        span(`class`:="icon icon-rocket"),
        raw("&nbsp; Containers")
      ),
      button(attrs.data.bind:="css: {active: page() === 'compose','btn-disabled':(!selectedMachine() || selectedMachine().state !== 'Running')}," +
        " click: function(){ selectPage('compose') }",
        title:="Docker compose projects management",
        `class`:="btn btn-default",
        span(`class`:="icon icon-pencil"),
        raw("&nbsp; Compose")
      )
    )
  }

  def machinesToolbar():Frag = {
    span(attrs.data.bind:="if: page()=='machines'",
      span(`class`:="span-toolbar-actions",
        button(`class`:="btn btn-default",title:="Run native Docker",
          span(`class`:="fa fa-docker"), attrs.data.bind:="click: startNativeDocker," +
            " css:{pressed: nativeDockerRunning(), 'btn-disabled': !nativeDockerAvailable()}",
          raw("&nbsp; Native")
        )
      ),
      span(`class`:="span-toolbar-actions",
        button(`class`:="btn btn-default",
          title:="Creates a new Docker machine",
          span(`class`:="icon icon-plus"), attrs.data.bind:="click: showNewMachine(), css:{'btn-disabled': nativeDockerRunning()}",
          "New Machine"
        )
      )
    )
  }

  def imagesToolbar():Frag = {
    span(`class`:="span-toolbar-actions",attrs.data.bind:="if: page()=='images'",
      button(`class`:="btn btn-default",
        title:="Pulls a new Docker image from an index",
        span(`class`:="icon icon-cloud"), attrs.data.bind:="click: pullImage",
        raw("&nbsp; Pull New")
      ),
      button(`class`:="btn btn-default",
        title:="Builds a new Docker image from a Dockerfile",
        span(`class`:="icon icon-doc-text"), attrs.data.bind:="click: buildImage",
        raw("&nbsp; Build New")
      ),
      button(`class`:="btn btn-default",
        title:="Searches for images in Docker Hub",
        span(`class`:="icon icon-search"), attrs.data.bind:="click: searchImage",
        raw("&nbsp; Search New")
      )
    )
  }

  def containersToolbar(): Frag = {
    span(`class`:="span-toolbar-actions",attrs.data.bind:="if: page()=='containers'",
      button(`class`:="btn btn-default",
        title:="Show logs for running containers",
        span(`class`:="icon icon-megaphone"), attrs.data.bind:="click: displayLogs, css:{pressed: $parent.displayContainerLogs()}",
        raw("&nbsp; Logs")
      )
    )
  }

  def composeToolbar(): Frag = {
    span(`class`:="span-toolbar-actions",attrs.data.bind:="if: page()=='compose'",
      button(`class`:="btn btn-default",
        title:="Loads a new Docker compose Project from a YAML file",
        span(`class`:="icon icon-plus"),title:="Loads a new Docker Compose project from a YAML file",
        attrs.data.bind:="click: loadComposeFile",
        raw("&nbsp; Load Project")
      )
    )
  }

  def rightButtons():Frag = {
    span(`class`:="span-toolbar-actions",
      button(`class`:="btn btn-default pull-right",
        attrs.data.bind:="click: navigateToCode",
        title:="See source code in Github",
        span(`class`:="icon icon-github")
      ),
      button(`class`:="btn btn-default pull-right",
        title:="Mondello settings",
        attrs.data.bind:="click: displaySettings",
        span(`class`:="icon icon-cog"),
        raw("&nbsp; Settings")
      ),
      button(`class`:="btn btn-default pull-right",
        title:="Docker index credentials management",
        attrs.data.bind:="click: displayLogin, css:{'btn-disabled':(!selectedMachine() || selectedMachine().state !== 'Running')}",
        span(`class`:="icon icon-users"),
        raw("&nbsp; Credentials")
      )
    )
  }

  // callbacks

  def selectPage(page:String) = this.page(page)

  def displayLogin() = showLogin(true)

  def displaySettings() = showSettings(true)

  def showNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      Log.trace("* show new machine dialog")
      newMachineDialog.show()
      ()
    }
  }

  def reload() = MondelloApp.reloadAll()

  def pullImage() = {
    Log.trace("* Donwload image dialog")
    PullImageDialog.show()
  }

  def buildImage() = {
    Log.trace("* Build image dialog")
    BuildImageDialog.show()
  }

  def displayLogs() = {
    Log.trace("* Display logs")
    val oldValue = displayContainerLogs()
    displayContainerLogs(!oldValue)
  }

  def searchImage() = {
    Log.trace("* Search image")
    SearchImageDialog.show()
  }

  def loadComposeFile() = {
    val result:js.UndefOr[js.Array[String]] = g.require("remote").dialog.showOpenDialog(
      js.Dictionary("title" -> "Loading Docker-Compose Project file", "properties" -> js.Array[String]("openFile"))
    ).asInstanceOf[js.UndefOr[js.Array[String]]]
    if(result.isDefined) {
      val filenames = result.get
      if(filenames.length > 0 ) {
        Settings.saveProject(filenames.head)
        Compose.reloadProjects()
      }
    }
  }

  def loadBuildFile() = {
    val result:js.UndefOr[js.Array[String]] = g.require("remote").dialog.showOpenDialog(
      js.Dictionary("title" -> "Select a Dockerfile to build", "properties" -> js.Array[String]("openFile"))
    ).asInstanceOf[js.UndefOr[js.Array[String]]]
    if(result.isDefined) {
      val filenames = result.get
      if(filenames.length > 0 ) {
        if (filenames.head.endsWith("Dockerfile")) {
          Log.trace(s"SELECTED: ${filenames.head}")
        } else {
          g.alert("You must select a Dockerfile")
        }
      }
    }
  }

  def navigateToCode() = {
    g.require("electron").shell.openExternal("https://github.com/antoniogarrote/mondello")
  }

  def startNativeDocker() = {
    Log.trace("** Start native Docker")
    MondelloApp.showModal("Starting native Docker daemon")
    val f = MondelloApp.startNative
    f.onSuccess{
      case (res:Boolean) =>
        MondelloApp.reloadNativeDocker()
        MondelloApp.closeModal()
    }

    f.onFailure {
      case e:Throwable =>
        g.alert(s"Error startin native Docker daemon: ${e.getMessage}")
        MondelloApp.closeModal()
    }
  }
}
