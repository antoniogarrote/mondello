package mondello.electron.components.pages.images

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.common.TableRenderer
import mondello.models.Image

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object SelectedImage extends KoComponent("selected-image") with TableRenderer {

  var selectedImage:KoObservable[Image] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
  }

  override def template: String = {
    span(
      span(attrs.data.bind:="if: selectedImage()",
        imageHeader(),
        firstSection(),
        configSection(),
        envSection(),
        exposedPortsSection(),
        volumesSection()
      ),
      span(attrs.data.bind:="ifnot: selectedImage()",
        div(id:="hero-outer",
          div(id:="hero-inner",
            img(src:="images/mondello.png")
          )
        )
      )
    ).toString()
  }

  private def imageHeader(): Frag = {
    div(`class`:="header-section",
      h2(attrs.data.bind:="text: selectedImage().idSmall"),
      h3(attrs.data.bind:="text: selectedImage().repository + ':' + selectedImage().tag")
    )
  }

  private def firstSection(): Frag = {
    val attributes = Seq(
      ("Id", "id"),
      ("Repository", "repository"),
      ("Tag", "tag"),
      ("Created", "createdAt"),
      ("Size", "size"),
      ("Docker Version", "inspect.DockerVersion"),
      ("Architecture", "inspect.Architecture"),
      ("Author", "inspect.Author"))
    makeTable("Attributes", "selectedImage()", attributes)
  }

  private def configSection(): Frag = {
    val attributes = Seq(
      ("Entry Point","inspect.Config.Entrypoint"),
      ("Command","inspect.Config.Cmd"),
      ("Hostname","inspect.Config.Hostname"),
      ("Domainname","inspect.Config.Domainname"),
      ("User","inspect.Config.User"),
      ("Attach Stdin","inspect.Config.AttachStdin"),
      ("Attach Stdout","inspect.Config.AttachStdout"),
      ("Attach Stderr","inspect.Config.AttachStderr"),
      ("TTY","inspect.Config.Tty"),
      ("Open Stdin","inspect.Config.OpenStdin"),
      ("Stdin Once","inspect.Config.StdinOnce"),
      ("Working Dir","inspect.Config.WorkingDir")
    )
    makeTable("Configuration", "selectedImage()", attributes)
  }

  private def envSection(): Frag = {
    div(`class`:="first-section",
      h4("Environment Variables"),
      table(`class`:="table-striped",
        thead(
          tr(
            th("Variable"),
            th("Value")
          )
        ),
        tbody(attrs.data.bind:="foreach: selectedImage().inspect.Config.Env",
          tr(
            td(attrs.data.bind:="text: $data.split('=')[0]"),
            td(attrs.data.bind:="text: $data.split('=')[1]")
          )
        )
      )
    )
  }

  private def exposedPortsSection(): Frag = {
    div(`class`:="first-section",
      h4("Exposed Ports"),
      table(`class`:="table-striped",
        thead(
          tr(
            th("Port")
          )
        ),
        tbody(attrs.data.bind:="foreach: Object.keys(selectedImage().inspect.Config.ExposedPorts || {})",
          tr(
            td(attrs.data.bind:="text: $data")
          )
        )
      )
    )
  }

  private def volumesSection(): Frag = {
    div(`class`:="first-section",
      h4("Volumes"),
      table(`class`:="table-striped",
        thead(
          tr(
            th("Volume")
          )
        ),
        tbody(attrs.data.bind:="foreach: Object.keys(selectedImage().inspect.Config.Volumes || {})",
          tr(
            td(attrs.data.bind:="text: $data")
          )
        )
      )
    )
  }
}