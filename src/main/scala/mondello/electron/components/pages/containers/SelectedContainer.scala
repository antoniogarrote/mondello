package mondello.electron.components.pages.containers

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.common.TableRenderer
import mondello.models.Container

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object SelectedContainer extends KoComponent("selected-container") with TableRenderer {

  var selectedContainer:KoObservable[Container] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedContainer = params("selectedContainer").asInstanceOf[KoObservable[Container]]
  }

  override def template: String = {
    span(
      span(attrs.data.bind:="if: selectedContainer()",
        containerHeader(),
        firstSection(),
        portsSection(),
        labelsSection()
      ),
      span(attrs.data.bind:="ifnot: selectedContainer()",
        div(id:="hero-outer",
          div(id:="hero-inner",
            img(src:="images/mondello.png")
          )
        )
      )
    ).toString()
  }

  private def containerHeader(): Frag = {
    div(`class`:="header-section",
      raw("<!-- ko if: selectedContainer() && selectedContainer().running -->"),
      span(`class`:="icon icon-record", style:="color:#34c84a"),
      raw("<!-- /ko -->"),
      raw("<!-- ko ifnot: selectedContainer() && selectedContainer().running -->"),
      span(`class`:="icon icon-record", style:="color:#fc605b"),
      raw("<!-- /ko -->"),
      h2(attrs.data.bind:="text: selectedContainer().id"),
      h3(attrs.data.bind:="text: selectedContainer().names")
    )
  }

  private def firstSection(): Frag = {
    val attributes = Seq(
      ("Id", "id"),
      ("Name", "names"),
      ("Command", "command"),
      ("Running for", "runningFor"),
      ("Status", "status"),
      ("Size", "size"))
    makeTable("Attributes", "selectedContainer()", attributes)
  }

  private def portsSection(): Frag = {
    makeKeyValueTable("Ports", ("Container","Host"), "selectedContainer().portsJs")
  }

  private def labelsSection(): Frag = {
    makeKeyValueTable("Labels", ("Key","Value"), "selectedContainer().labelsJs")
  }


}
