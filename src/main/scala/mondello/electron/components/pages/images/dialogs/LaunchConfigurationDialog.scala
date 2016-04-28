package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object LaunchConfigurationDialog extends KoComponent("launch-configuration-dialog") {
  val name:KoObservable[String] = Ko.observable("")
  val command:KoObservable[String] = Ko.observable("")
  val entryPoint:KoObservable[String] = Ko.observable("")
  val link:KoObservable[String] = Ko.observable("")
  val expose:KoObservable[String] = Ko.observable("")
  val publish:KoObservable[String] = Ko.observable("")
  val envVars:KoObservable[String] = Ko.observable("")

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="launch-configuration-dialog", `class`:="floating-window", style:="display: none",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Launch Configuration")
      ),
      div(`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Name"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: name")
          ),
          div(`class`:="form-group",
            label("Command"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: command")
          ),
          div(`class`:="form-group",
            label("Entry Point"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: entryPoint")
          ),
          div(`class`:="form-group",
            label("Links"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: link")
          ),
          div(`class`:="form-group",
            label("Expose Ports"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: expose")
          ),
          div(`class`:="form-group",
            label("Publish Ports"),
            input(id:="new-machine-name", `class`:="form-control",attrs.data.bind:="value: publish")
          ),
          div(`class`:="form-group",
            label("Environment Vars"),
            textarea(rows:="3",`class`:="form-control",attrs.data.bind:="value: envVars")
          ),
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-primary pull-right",
              attrs.data.bind:="click:submitLaunchConfig",
              "Ok")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // Utility functions

  def show() = {
    g.$("#launch-configuration-dialog").show()
  }

  def hide() = {
    g.$("#launch-configuration-dialog").hide()
  }

  // callbacks

  def submitLaunchConfig() = {
    println("* Submit launch config")
    hide()
  }
}
