package mondello.electron.components

import knockout.tags.KoText
import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.models.Machine
import mondello.proxies.DockerMachine
import mondello.config.Environment
import mondello.electron.components.pages.Machines

import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import mondello.platform.js.Implicits.ConsoleProcess

import scalatags.Text.all._

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


  nestedComponents += (
    "machines" -> Machines(dockerMachine),
    "toolbar" -> Toolbar
    )

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="main",`class`:="window",
      Toolbar.tag(KoText.all.params:="selectedMachine: selectedMachine, page: page"),
      Machines.tag(KoText.all.params:="machines: dockerMachines, loadingMachines: loadingMachines, selectedMachine: selectedMachine")
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