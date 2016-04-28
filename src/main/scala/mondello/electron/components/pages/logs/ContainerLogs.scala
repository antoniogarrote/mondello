package mondello.electron.components.pages.logs

import knockout.{KoComponent, KoObservable, KoObservableArray}
import mondello.electron.components.Toolbar
import mondello.models.Container

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ContainerLogs extends KoComponent("container-logs") {

  var displayContainerLogs:KoObservable[Boolean] = null
  var containers:KoObservableArray[Container] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    displayContainerLogs = params("displayContainerLogs").asInstanceOf[KoObservable[Boolean]]
    Toolbar.displayContainerLogs = displayContainerLogs
    containers = params("containers").asInstanceOf[KoObservableArray[Container]]
  }

  override def template: String = {
    div(`class`:="upper",
      table(id:="log-container-selection",`class`:="table-striped",
        thead(
          tr(
            th(),
            th("Id"),
            th("Label"),
            th("Command"),
            th("Created At"),
            th("Running For")
          )
        ),
        tbody(attrs.data.bind:="foreach: containers",
          tr(attrs.data.bind:="if: $data.running",
            td(
              input(attrs.data.bind:="form-control", `type`:="checkbox",
                attrs.data.bind:="click: $parent.startLoggingContainer()"
              )
            ),
            td(attrs.data.bind:="text:$data.id"),
            td(attrs.data.bind:="text:$data.names"),
            td(attrs.data.bind:="text:$data.command"),
            td(attrs.data.bind:="text:$data.createdAt"),
            td(attrs.data.bind:="text:$data.runningFor")
          )
        )
      )
    ).toString() ++ div(id:="console",`class`:="lower",
      p("# select running machines to start tailing logs")
    ).toString()
  }

  def startLoggingContainer():KoCallback[Container] = koCallback({ (container) =>
    println(s"* Start logging container: ${container.id}")
  })
}
