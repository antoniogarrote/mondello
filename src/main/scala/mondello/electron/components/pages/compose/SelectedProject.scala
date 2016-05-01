package mondello.electron.components.pages.compose

import knockout.{KoComponent, KoObservable}
import mondello.config.Log
import mondello.electron.components.common.TableRenderer
import mondello.models.{Project, Service}

import scala.collection.mutable
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.annotation.JSExportAll
import scalatags.Text.all._
import scalatags.Text.attrs
import scala.scalajs.js.Dynamic.{global => g}


@JSExportAll
object SelectedProject extends KoComponent("selected-project") with TableRenderer {

  var selectedProject:KoObservable[Project] = null
  var selectedServices:mutable.Map[String,Service] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedProject = params("selectedProject").asInstanceOf[KoObservable[Project]]
    selectedServices = params("selectedServices").asInstanceOf[mutable.Map[String,Service]]
    selectedProject.subscribe( (_:Project) => selectedServices.clear())
  }

  override def template: String = {
    span(
      span(attrs.data.bind:="if: selectedProject()",
        projectsListing(),
        div(id:="servicesInfo",`class`:="lower pane padded-more",
          attrs.data.bind:="foreach: (selectedProject() ? selectedProject().servicesJS : [])",
          projectHeader(),
          envSection(),
          portsSection(),
          servicesSection(),
          volumesSection()
        )
      ),
      span(attrs.data.bind:="ifnot: selectedProject()",
        div(id:="hero-outer",
          div(id:="hero-inner",
            img(src:="images/mondello.png")
          )
        )
      )
    ).toString()
  }

  private def projectsListing(): Frag = {
    div(`class`:="upper",
      table(id:="services-selection", `class`:="table-stripped",
        thead(
          tr(
            th(style:="min-width: 40px",
              input(`class`:="form-control",`type`:="checkbox",id:="select-all-input",
                attrs.data.bind:="click: selectAllServices")
            ),
            th("Id"),
            th("Source"),
            th("Origin"),
            th("Ports Mapped"),
            th("Number of Links"),
            th("Number of Env Vars")
          )
        ),
        tbody(attrs.data.bind:="foreach: selectedProject().servicesJS",
          tr(
            th(
              input(`class`:="form-control",`type`:="checkbox",id:="select-all-input",
                attrs.data.bind:="click: $parent.selectService()")
            ),
            th(attrs.data.bind:="text: id"),
            th(attrs.data.bind:="text: source"),
            th(attrs.data.bind:="text: from"),
            th(attrs.data.bind:="text: portsCount"),
            th(attrs.data.bind:="text: linksCount"),
            th(attrs.data.bind:="text: volumesCount")
          )
        )
      )
    )
  }

  private def projectHeader(): Frag = {
    div(`class`:="header-section",attrs.data.bind:="attr:{id: 'service-'+id}",
      span(`class`:="icon icon-box"),
      h2(attrs.data.bind:="text: id"),
      h3(attrs.data.bind:="text: source +':' + from")
    )
  }

  private def envSection(): Frag = makeArrayMapTable("Env. Variables","environmentJS",Seq(("Variable","key"),("Value","val")))

  private def portsSection(): Frag = makeArrayMapTable("Ports Mapping","portsJS",Seq(("From","from"),("To","to")))

  private def servicesSection(): Frag = makeArrayTable("Linked Services","externalLinks", "Service")

  private def volumesSection(): Frag = makeArrayMapTable("Volumes Mounted","volumesJS",Seq(("Container Dir.","container"),("Host Dir.","host")))


  // callbacks

  def selectAllServices()= {
    Log.trace("* select all services")
    val oldValue = g.$("#select-all-input").prop("checked").asInstanceOf[Boolean]
    g.$("#services-selection th input").prop("checked",oldValue)
    if(oldValue) {
      for(service <- selectedProject().services)
        selectedServices.update(service.id, service)
    } else {
      for(service <- selectedProject().services)
        selectedServices.remove(service.id)
    }
    true
  }

  def selectService():KoCallback[Service] = koCallback { (service) =>
    Log.trace(s"* Selecting service ${service.id} ")
    if(selectedServices.contains(service.id)) {
      selectedServices.remove(service.id)
    } else {
      selectedServices.update(service.id, service)
    }
    true
  }


}
