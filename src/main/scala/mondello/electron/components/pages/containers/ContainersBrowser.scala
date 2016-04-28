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
object ContainersBrowser extends KoComponent("containers-browser") {

  var containers:KoObservableArray[Container] = null
  var selectedContainer:KoObservable[Container] = null
  var loadingContainers:KoObservable[Boolean] = null

  val searchList = new SearchableList[Container] {
    override def isResult(container: Container, searchText: String): Boolean =  {
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

  override def viewModel(params: Dictionary[Any]): Unit = {
    loadingContainers = params("loadingContainers").asInstanceOf[KoObservable[Boolean]]
    selectedContainer = params("selectedContainer").asInstanceOf[KoObservable[Container]]
    containers = params("containers").asInstanceOf[KoObservableArray[Container]]
    searchList.subscribe(containers)
  }

  override def template: String = {
    ul(`class`:="list-group",
      li(`class`:="list-group-header",
        input(id:="containerSearchBox",`class`:="form-control", `type`:="text", placeholder:="Containers Search",
          attrs.data.bind:="textInput: searchList.elementSearch"
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
      raw("<!-- ko foreach: searchList.searchResults -->"),
      li(`class`:="list-group-item",
        attrs.data.bind:="click: $parent.selectContainer.bind($parent,$data),"++
          "visible: !$parent.loadingContainers(),"++
          "css: {active: ($parent.selectedContainer() && $parent.selectedContainer().id == id)}",
        div(`class`:="media-body",
          raw("<!-- ko if: running -->"),
          span(`class`:="icon icon-record", style:="color:#34c84a"),
          raw("<!-- /ko -->"),
          raw("<!-- ko ifnot: running -->"),
          span(`class`:="icon icon-record", style:="color:#fc605b"),
          raw("<!-- /ko -->"),
          raw("&nbsp;"),
          span(`class`:="icon icon-rocket"),
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

}
