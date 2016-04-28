package mondello.electron.components.pages

import knockout._
import knockout.tags.KoText
import mondello.electron.components.pages.containers.{ContainerFooter, ContainersBrowser, SelectedContainer}
import mondello.models.Container
import mondello.proxies.Docker

import scala.scalajs.js.Dynamic.{global => g}
import scala.concurrent.Promise
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportAll
object Containers extends KoComponent("docker-containers"){

  var docker:KoComputed[Docker] = null
  var containers:KoObservableArray[Container] = Ko.observableArray()
  var selectedContainer:KoObservable[Container] = Ko.observable(null)
  var loadingContainers:KoObservable[Boolean] = Ko.observable(false)


  nestedComponents += (
    "ContainerFooter" -> ContainerFooter,
    "ContainerBrowser" -> ContainersBrowser,
    "SelectedContainer" -> SelectedContainer
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = params("docker").asInstanceOf[KoComputed[Docker]]
    this.docker.subscribe((_:Docker) => reloadContainers())
  }

  override def template: String = {
    div(`class`:="window-content",
      div(`class`:="pane-group",
        ContainersBrowser.tag(`class`:="pane pane-sm sidebar",
          KoText.all.params:="loadingContainers: loadingContainers, selectedContainer: selectedContainer, containers: containers"),
        SelectedContainer.tag(`class`:="pane padded-more",
          KoText.all.params:="selectedContainer: selectedContainer")
      ),
      ContainerFooter.tag(`class`:="toolbar-footer",
        KoText.all.params:="selectedContainer: selectedContainer")
    ).toString()
  }

  def reloadContainers() = {
    println("*** Reloading Docker Containers")
    loadingContainers(true)
    if(docker() == null) {
      containers.removeAll()
      val p = Promise[List[Container]]()
      p.success(List())
      loadingContainers(false)
      p.future
    } else {
      val f = docker().containers
      f.onSuccess {
        case newContainers =>
          var foundSelectedContainer = false
          containers.removeAll()
          newContainers.foreach({ (Container: Container) =>
            if (selectedContainer() != null && selectedContainer().id == Container.id) {
              foundSelectedContainer = true
              selectedContainer(Container)
            }
            containers.push(Container)
          })
          if (!foundSelectedContainer) selectedContainer(null)
      }
      f.onFailure {
        case e => g.alert(s"!!! Error loading Containers $e")
      }
      // finished loading
      f.onComplete((_) => loadingContainers(false))
      f
    }
  }

}
