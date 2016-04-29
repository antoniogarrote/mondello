package mondello.electron.components.pages

import knockout.tags.KoText
import knockout._
import mondello.config.Settings
import mondello.electron.components.pages.compose.{ProjectFooter, ProjectsBrowser, SelectedProject}
import mondello.models.Project
import mondello.proxies.DockerCompose

import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
object Compose extends KoComponent("docker-compose") {


  var dockerCompose:KoComputed[DockerCompose] = null
  val projects:KoObservableArray[Project] = Ko.observableArray[Project]()
  val loadingProjects:KoObservable[Boolean] = Ko.observable(false)
  val selectedProject:KoObservable[Project] = Ko.observable(null)

  nestedComponents += (
    "ProjectsBrowser" -> ProjectsBrowser,
    "SelectedProject" -> SelectedProject,
    "ProjectFooter" -> ProjectFooter
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    dockerCompose = params("dockerCompose").asInstanceOf[KoComputed[DockerCompose]]
  }

  override def template: String = {
    div(`class`:="window-content",
      div(`class`:="pane-group",
        ProjectsBrowser.tag(`class`:="pane pane-sm sidebar",
        KoText.all.params:=s"loadingProjects: loadingProjects, selectedProject: selectedProject, projects: projects"),
        SelectedProject.tag(`class`:="pane padded-more",
          KoText.all.params:="selectedProject: selectedProject")
      ),
      ProjectFooter.tag(`class`:="toolbar-footer",
        KoText.all.params:="selectedProject: selectedProject")
    ).toString()
  }

  def reloadProjects() = {
    println("* Reloading projects")
    projects.removeAll()
    var foundProject = false
    Settings.compose.foreach { (projectFile) =>
      Project.load(projectFile).foreach[Unit] { (project) =>
        if(selectedProject() != null && selectedProject().file == project.file) {
          foundProject = true
          selectedProject(project)
        }
        projects.push(project)
      }
    }
    if(!foundProject) { selectedProject(null) }
  }
}
