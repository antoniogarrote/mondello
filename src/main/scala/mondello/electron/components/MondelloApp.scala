package mondello.electron.components

import knockout.tags.KoText
import knockout._
import mondello.models.Machine
import mondello.proxies.{Docker, DockerMachine}
import mondello.config.Environment
import mondello.electron.components.pages.{Machines,Images}

import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import mondello.platform.js.Implicits.ConsoleProcess

import scalatags.Text.all._
import scalatags.Text.attrs

@ScalaJSDefined
object MondelloApp extends KoComponent {

  @JSName("buildMondelloApp")
  def apply(): this.type = {
    loadingMachines(true)
    Machines(dockerMachine).reloadMachines()
    this
  }

  override val tagName: String = "mondello-app"
  val tag = KoComponent.mkTag(tagName)
  val page: KoObservable[String] = Ko.observable("machines")

  val env:Environment = Environment.defaultEnv

  val dockerMachine = new DockerMachine(env)

  val selectedMachine: KoObservable[Machine] = Ko.observable(null)
  val loadingMachines: KoObservable[Boolean] = Ko.observable(false)
  val dockerMachines: KoObservableArray[Machine] = Ko.observableArray[Machine]()
  var docker:KoComputed[Docker] = null

  nestedComponents += (
    "machines" -> Machines(dockerMachine),
    "images" -> Images,
    "toolbar" -> Toolbar
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = Ko.computed({() =>
      if (selectedMachine() != null)
        new Docker(selectedMachine().name, env)
      else
        null
    })
  }

  override def template: String = {
    div(id:="main",`class`:="window",
      Toolbar.tag(KoText.all.params:="selectedMachine: selectedMachine, page: page"),
      Machines.tag(
        KoText.all.params:="machines: dockerMachines, loadingMachines: loadingMachines, selectedMachine: selectedMachine",
        attrs.data.bind:="visible: page()=='machines'"
      ),
      Images.tag(
        KoText.all.params:="docker: docker",
        attrs.data.bind:="visible: page()=='images'"
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