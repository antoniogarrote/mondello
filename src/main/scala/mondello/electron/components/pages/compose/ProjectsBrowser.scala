package mondello.electron.components.pages.compose

import knockout.{KoComponent, KoObservable, KoObservableArray}
import mondello.electron.components.common.SearchableList
import mondello.models.Project

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ProjectsBrowser extends KoComponent("comppose-projects") {

  var projects:KoObservableArray[Project] = null
  var selectedProject:KoObservable[Project] = null
  var loadingProjects: KoObservable[Boolean] = null

  val searchList = new SearchableList[Project] {
    override def isResult(project: Project, searchText: String): Boolean = {
      val isMatch = subString(searchText)(_)
      isMatch { project.file } ||
      project.services.map {(service) =>
        isMatch { service.from } ||
        isMatch { service.id } ||
        isMatch { service.externalLinks.mkString("") } ||
        isMatch { service.environment.toString } ||
        isMatch { service.links.mkString("") } ||
        isMatch { service.volumes.toString } ||
        isMatch { service.source }
      }.reduce(_ || _)
    }
  }


  override def viewModel(params: Dictionary[Any]): Unit = {
    loadingProjects = params("loadingProjects").asInstanceOf[KoObservable[Boolean]]
    selectedProject = params("selectedProject").asInstanceOf[KoObservable[Project]]
    projects = params("projects").asInstanceOf[KoObservableArray[Project]]
    searchList.subscribe(projects)
  }

  override def template: String = {
    ul(`class`:="list-group",attrs.data.bind:="css:{'list-group-with-elems':!loadingProjects()}",
      li(`class`:="list-group-header",
        input(id:="projectSearchBox",`class`:="form-control", `type`:="text", placeholder:="Projects Search",
          attrs.data.bind:="textInput: searchList.elementSearch"
        )
      ),
      // Not Projects
      li(`class`:="list-group-item",
        attrs.data.bind:="visible: loadingProjects()",
        div(`class`:="media-body",
          div(`class`:="spinner",
            div(`class`:="rect1"),
            div(`class`:="rect2"),
            div(`class`:="rect3"),
            div(`class`:="rect4"),
            div(`class`:="rect5")
          ))
      ),
      // Images
      raw("<!-- ko foreach: searchList.searchResults -->"),
      li(`class`:="list-group-item",
        attrs.data.bind:="click: $parent.selectProject(),"++
          "visible: !$parent.loadingProjects(),"++
          "css: {active: ($parent.selectedProject() && $parent.selectedProject().file == file)}",
        div(`class`:="media-body",
          span(`class`:="icon icon-pencil"),
          raw("&nbsp;"),
          strong(attrs.data.bind:="text: filename"),
          p(attrs.data.bind:="text: localDir"),
          p(attrs.data.bind:="text: servicesCount +' services'")
        )
      ),
      raw("<!-- /ko -->")
    ).toString()
  }

  // Callbacks

  def selectProject():KoCallback[Project] = koCallback(this.selectedProject(_))
}
