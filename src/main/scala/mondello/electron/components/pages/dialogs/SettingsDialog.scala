package mondello.electron.components.pages.dialogs

import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.config.Settings

import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object SettingsDialog extends KoComponent("settings") {

  var showSettings:KoObservable[Boolean] = null
  val dockerHome:KoObservable[String] = Ko.observable(Settings.dockerHome)
  val driversHome:KoObservable[String] = Ko.observable(Settings.driversHome)

  override def viewModel(params: Dictionary[Any]): Unit = {
    showSettings = params("showSettings").asInstanceOf[KoObservable[Boolean]]
  }

  override def template: String = {
    div(id:="settings-dialog",`class`:="floating-window",style:="display:none",
      attrs.data.bind:="visible: showSettings",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Mondello Settings")
      ),
      div(`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Docker Home Path"),
            input(`class`:="form-control",
              attrs.data.bind:="value: dockerHome()")
          ),
          div(`class`:="form-group",
            label("Drivers Home Path"),
            input(`class`:="form-control",
              attrs.data.bind:="value: driversHome()")
          ),
          form(`class`:="padded-less",
            div(`class`:="form-group",
              div(`class`:="form-actions",
                button(`type`:="submit",`class`:="btn-cancel btn btn-form btn-default pull-right",
                  attrs.data.bind:="click: hideSettings",
                  "Cancel"),
                button(`type`:="submit",`class`:="btn-ok btn btn-form btn-default pull-right",
                  attrs.data.bind:="click: okSettings",
                  "Ok")
              )
            )
          )
        )
      )
    ).toString()
  }

  // Callbacks

  def hideSettings() = {
    showSettings(false)
  }

  def okSettings() = {
    Settings.dockerHome = dockerHome()
    Settings.driversHome = driversHome()
    Settings.persist()
    showSettings(false)
  }
}
