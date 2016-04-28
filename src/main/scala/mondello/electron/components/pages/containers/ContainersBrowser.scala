package mondello.electron.components.pages.containers

import knockout.{KoComponent, KoObservable, KoObservableArray}
import mondello.electron.components.common.SearchableList
import mondello.models.Container

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ContainersBrowser extends KoComponent with SearchableList[Container] {
  override val tagName: String = "containers-browser"
  val tag = KoComponent.mkTag(tagName)

  var containers:KoObservableArray[Container] = null
  var selectedContainer:KoObservable[Container] = null
  var loadingContainers:KoObservable[Boolean] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    loadingContainers = params("loadingContainers").asInstanceOf[KoObservable[Boolean]]
    selectedContainer = params("selectedContainer").asInstanceOf[KoObservable[Container]]
    containers = params("containers").asInstanceOf[KoObservableArray[Container]]
    subscribe(containers)
  }

  override def template: String = {
    ul(`class`:="list-group",
      li(`class`:="list-group-header",
        input(id:="containerSearchBox",`class`:="form-control", `type`:="text", placeholder:="Containers Search",
          attrs.data.bind:="textInput: elementSearch"
        )
      ),
      // No Containers
      li(`class`:="list-group-item",
        attrs.data.bind:="visible: loadingContainers()",
        div(`class`:="media-body",
          div(`class`:="spinner",
            div(`class`:="rect1"),
            div(`class`:="rect2"),
            div(`class`:="rect3"),
            div(`class`:="rect4"),
            div(`class`:="rect5")
          ))
      ),
      // Containers
      raw("<!-- ko foreach: searchResults -->"),
      li(`class`:="list-group-item",
        attrs.data.bind:="click: $parent.selectContainer.bind($parent,$data),"++
          "visible: !$parent.loadingContainers(),"++
          "css: {active: ($parent.selectedContainer() && $parent.selectedContainer().id == id)}",
        div(`class`:="media-body",
          span(`class`:="icon icon-box"),
          raw("&nbsp;"),
          strong(attrs.data.bind:="text: id"),
          p(attrs.data.bind:="text: names")
        )
      ),
      raw("<!-- /ko -->")
    ).toString()
  }

  // Callbacks

  def selectContainer(container:Container, event:js.Any) = {
    this.selectedContainer(container)
  }

  // Helper functions

  override def isResult(container: Container, searchText: String): Boolean = {
    val searchMatch = subString(searchText)(_)
    searchMatch { container.command } ||
      searchMatch { container.createdAt } ||
      searchMatch { container.id } ||
      searchMatch { container.image } ||
      searchMatch { container.labels.toString() } ||
      searchMatch { container.names.toString } ||
      searchMatch { container.ports.toString() } ||
      searchMatch { container.status }
  }
}
