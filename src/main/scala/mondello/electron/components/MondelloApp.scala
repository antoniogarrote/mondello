package mondello.electron.components

import knockout.tags.KoText
import knockout._
import mondello.models.{Machine, Project}
import mondello.proxies.{Docker, DockerCompose, DockerMachine}
import mondello.config.{Environment, Settings}
import mondello.electron.components.pages.{Compose, Containers, Images, Machines}

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSName, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import mondello.platform.js.Implicits.ConsoleProcess

import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object MondelloApp extends KoComponent("mondello-app") {

  @JSExport("apply")
  def apply(settings:Settings.type): this.type = {
    this.settings = settings
    env = settings.toEnv
    dockerMachine = new DockerMachine(env)
    loadingMachines(true)
    Machines(dockerMachine).reloadMachines()
    Compose.reloadProjects()
    this
  }

  val page: KoObservable[String] = Ko.observable("machines")

  var settings:Settings.type = null
  var env:Environment = null

  var dockerMachine:DockerMachine = null

  val selectedMachine: KoObservable[Machine] = Ko.observable(null)
  val loadingMachines: KoObservable[Boolean] = Ko.observable(false)
  val dockerMachines: KoObservableArray[Machine] = Ko.observableArray[Machine]()
  val displayContainerLogs: KoObservable[Boolean] = Ko.observable(false)
  var docker:KoComputed[Docker] = null
  var dockerCompose:KoComputed[DockerCompose] = null

  nestedComponents += (
    "Machines" -> Machines(dockerMachine),
    "Images" -> Images,
    "Containers" -> Containers,
    "Compose" -> Compose,
    "Toolbar" -> Toolbar
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = Ko.computed({() =>
      if (selectedMachine() != null && selectedMachine().state == "Running")
        new Docker(selectedMachine().name, env)
      else
        null
    })

    this.dockerCompose = Ko.computed({() =>
      if (selectedMachine() != null && selectedMachine().state == "Running")
        new DockerCompose(selectedMachine().name, env)
      else
        null
    })
  }

  override def template: String = {
    div(id:="main",`class`:="window",
      Toolbar.tag(KoText.all.params:="selectedMachine: selectedMachine, page: page, displayContainerLogs: displayContainerLogs"),
      Machines.tag(
        KoText.all.params:="machines: dockerMachines, loadingMachines: loadingMachines, selectedMachine: selectedMachine",
        attrs.data.bind:="visible: page()=='machines'"
      ),
      Images.tag(
        KoText.all.params:="docker: docker",
        attrs.data.bind:="visible: page()=='images'"
      ),
      Containers.tag(
        KoText.all.params:="docker: docker, displayContainerLogs: displayContainerLogs",
        attrs.data.bind:="visible: page()=='containers'"
      ),
      Compose.tag(
        KoText.all.params:="dockerCompose: dockerCompose",
        attrs.data.bind:="visible: page()=='compose'"
      )
    ).toString()
  }

  // Modal Windows
  def showModal(title:String) = {
    g.$("#modal-title").text(title)
    g.$("#modal-close").show()
    g.$("#modal").show()
  }

  def closeModal() = {
    g.$("#modal").hide()
  }

}