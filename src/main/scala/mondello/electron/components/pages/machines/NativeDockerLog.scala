package mondello.electron.components.pages.machines

import knockout.{KoComponent, KoObservableArray}
import mondello.config.Log

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.{TypedTag, attrs}


@JSExportAll
object NativeDockerLog extends KoComponent("native-docker-logs") {

  var nativeDockerLog:KoObservableArray[String] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    nativeDockerLog = params("nativeDockerLog").asInstanceOf[KoObservableArray[String]]
  }

  override def template: String = {
    div(id:="console",
      p("# Native Docker running, trying to tail logs..."),
      raw("<!-- ko foreach: nativeDockerLog -->"),
      div(attrs.data.bind:="html: $data"),
      raw("<!-- /ko -->")
    ).toString()
  }
}
