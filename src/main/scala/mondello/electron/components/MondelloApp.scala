package mondello.electron.components

import knockout.tags.KoText
import knockout._
import mondello.models.Machine
import mondello.proxies.{Docker, DockerMachine}
import mondello.config.Environment
import mondello.electron.components.pages.{Containers, Images, Machines}

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
  def apply(): this.type = {
    loadingMachines(true)
    Machines(dockerMachine).reloadMachines()
    this
  }

  val page: KoObservable[String] = Ko.observable("machines")

  val env:Environment = Environment.defaultEnv

  val dockerMachine = new DockerMachine(env)

  val selectedMachine: KoObservable[Machine] = Ko.observable(null)
  val loadingMachines: KoObservable[Boolean] = Ko.observable(false)
  val dockerMachines: KoObservableArray[Machine] = Ko.observableArray[Machine]()
  val displayContainerLogs: KoObservable[Boolean] = Ko.observable(false)
  var docker:KoComputed[Docker] = null

  nestedComponents += (
    "Machines" -> Machines(dockerMachine),
    "Images" -> Images,
    "Containers" -> Containers,
    "Toolbar" -> Toolbar
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = Ko.computed({() =>
      if (selectedMachine() != null && selectedMachine().state == "Running")
        new Docker(selectedMachine().name, env)
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