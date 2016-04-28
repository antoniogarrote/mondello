package mondello.electron.components.pages.logs

import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.electron.components.Toolbar
import mondello.electron.components.common.ColorGenerator
import mondello.electron.components.pages.Containers
import mondello.models.Container

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

import scala.scalajs.js.Dynamic.{global => g}

@JSExportAll
object ContainerLogs extends KoComponent("container-logs") with ColorGenerator {

  var displayContainerLogs:KoObservable[Boolean] = null
  var containers:KoObservableArray[Container] = null
  var bufferSize:KoObservable[Integer] = null
  val childrenRegistry = mutable.Map[String,js.Any]()
  val logs:KoObservableArray[String] = Ko.observableArray()

  override def viewModel(params: Dictionary[Any]): Unit = {
    displayContainerLogs = params("displayContainerLogs").asInstanceOf[KoObservable[Boolean]]
    bufferSize = params("bufferSize").asInstanceOf[KoObservable[Integer]]
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
                attrs.data.bind:="click: $parent.startLoggingContainer(), attr: {id: 'container-log-'+$data.id}"
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
      p("# select running machines to start tailing logs"),
      raw("<!-- ko foreach: logs -->"),
      span(attrs.data.bind:="html: $data"),
      raw("<!-- /ko -->")
    ).toString() ++ footer(id:="logs-toolbar",`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: clearLog()",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Clear"
        ),
        span(id:="log-search-box",`class`:="form-group",
          input(`class`:="form-control", placeholder:="Search Text")
        ),
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: searchLog()",
          span(`class`:="icon icon-search"),
          raw("&nbsp;"),
          "Search"
        ),
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: cancelSearchLog()",
          span(`class`:="icon icon-cancel-circled"),
          raw("&nbsp;"),
          "Cancel"
        )
      )
    ).toString()
  }

  def clearLog():KoCallback[js.Any] = koCallback({(_) =>
    println("** Clearing log")
  })

  def searchLog():KoCallback[String] = koCallback({(searchText) =>
    println(s"** Searching log for $searchText")
  })

  def cancelSearchLog():KoCallback[js.Any] = koCallback({(_) =>
    println("** Cancel search log")
  })

  def startLoggingContainer():KoCallback[Container] = koCallback({ (container) =>
    println(s"* Start logging container: ${container.id}")
    if(!childrenRegistry.contains(container.id)) {
      val color = nextColor()
      val child = Containers.logContainer(container, { (data) =>
        if(data == null){
          // the process died
          childrenRegistry.remove(container.id)
        }else {
          // got some more data
          val tokenized = tokenizeData(data)
          var html = "<p style='color:" + color + "'>=====" + container.id + " / " + container.names + "</p>"
          html = html + "<p style='color:" + color + "'>" + tokenized + "</p>"
          logs.push(html)
          if (logs().length > bufferSize()) logs.shift()
        }
      })
      childrenRegistry.update(container.id, child)
    } else {
      val child = childrenRegistry.remove(container.id).get
      println(s"* Killing log observer child for container ${container.id}")
      try {
        child.asInstanceOf[js.Dynamic].kill()
      } catch {
        case e:Throwable => println(s"!! Error killing log observer $e")
      }
    }
    true
  })

  protected def tokenizeData(data:String):String = {
    data.replace("\n","<br/>").split("\\s").map { (token) =>
      if(token == "") {
        token
      } else {
        "<span class='token'>"+token+"</span>"
      }
    }.mkString("&nbsp;")
  }
}
