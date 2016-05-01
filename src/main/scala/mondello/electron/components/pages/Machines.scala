package mondello.electron.components.pages

import knockout.tags.KoText.all._
import knockout.{KoComponent, KoObservable, KoObservableArray}
import mondello.config.Log
import mondello.electron.components.pages.machines.{MachineFooter, MachinesBrowser}
import mondello.models.Machine
import mondello.proxies.DockerMachine

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSName, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary, Dynamic}
import scala.util.Try
import scala.concurrent.Future
import scalatags.Text.all._
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


@JSExportAll
object Machines extends KoComponent("docker-machines") {

  @JSExport("apply")
  def apply(dockerMachine:DockerMachine): Machines.type = {
    this.dockerMachine = dockerMachine
    this
  }

  var dockerMachine:DockerMachine = null
  var loadingMachines:KoObservable[Boolean] = null
  var dockerMachines:KoObservableArray[Machine] = null
  var selectedMachine:KoObservable[Machine] = null


  nestedComponents += (
    "selected" -> mondello.electron.components.pages.machines.SelectedMachine,
    "browser" -> MachinesBrowser,
    "footer" -> MachineFooter
  )


  override def viewModel(params: Dictionary[Any]): Unit = {
    dockerMachines = params("machines").asInstanceOf[KoObservableArray[Machine]]
    loadingMachines = params("loadingMachines").asInstanceOf[KoObservable[Boolean]]
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[Machine]]
  }

  override def template: String = {
    div(`class`:="window-content",
      div(`class`:="pane-group",
        MachinesBrowser.tag(
          `class`:="pane pane-sm sidebar",
          params:="machines: dockerMachines, loadingMachines: loadingMachines, selectedMachine: selectedMachine"),
        mondello.electron.components.pages.machines.SelectedMachine.tag(
          `class`:="pane padded-more",
          params:="selectedMachine: selectedMachine"
        )
      ),
      MachineFooter.tag(`class`:="toolbar-footer",
        params:="selectedMachine: selectedMachine"
      )
    ).toString()
  }

  def reloadMachines() = {
    Log.trace("*** Reloading Docker Machines")
    val f = dockerMachine.all
    f.onSuccess {
      case machines =>
        var foundSelectedMachine = false
        dockerMachines.removeAll()
        machines.foreach({ (machine:Machine) =>
          if(selectedMachine() != null && machine.name == selectedMachine().name) {
            foundSelectedMachine = true
            selectedMachine(machine)
          }
          dockerMachines.push(machine)
        })
        if(!foundSelectedMachine) selectedMachine(null)
    }
    f.onFailure {
      case e => g.alert(s"!!! Error loading machines $e")
    }
    // finished loading
    f.onComplete((_) => loadingMachines(false))
    f
  }

  def startMachine(machine:mondello.models.Machine, cb:(Boolean)=>Unit=null): Future[Boolean] = {
    runMachineCommand[Machine](machine, (machine) => dockerMachine.start(machine.name), cb)
  }

  def stopMachine(machine:mondello.models.Machine, cb:(Boolean)=>Unit=null): Future[Boolean] = {
    runMachineCommand[Machine](machine, (machine) => dockerMachine.stop(machine.name), cb)
  }

  def removeMachine(machine: Machine, cb: (Boolean) => Unit=null):Future[Boolean] = {
    runMachineCommand[Machine](machine, (machine) => dockerMachine.remove(machine.name), cb)
  }

  def createMachine(name:String, driver:String, labels:List[String], envs:List[String], cb:(Boolean)=>Unit=null): Future[Boolean] = {
    runMachineCommand[(String,String,List[String],List[String])](
      (name, driver, labels, envs),
      {case (n,d,ls,es) => dockerMachine.newMachine(n,d,ls,es)},
      cb)
  }


  private def runMachineCommand[T](machine:T, cmd:(T)=>Future[Boolean], cb:(Boolean)=>Unit=null): Future[Boolean] = {
    val f:Future[Boolean] = cmd(machine)
    f.onSuccess {
      case result =>
        reloadMachines().andThen[Unit]({case (_:Try[List[mondello.models.Machine]]) => if(cb != null) cb(true)})
        true
    }

    f.onFailure {
      case e =>
        g.alert(e.getMessage)
        if(cb != null) cb(false)
        false
    }

    f
  }

}
