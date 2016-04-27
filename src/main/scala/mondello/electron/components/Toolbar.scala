package mondello.electron.components

import knockout.{KoComponent, KoObservable}
import mondello.models.Machine

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@ScalaJSDefined
object Toolbar extends KoComponent {

  override val tagName: String = "toolbar"
  val tag = KoComponent.mkTag(tagName)

  var page:KoObservable[String] = null
  var selectedMachine:KoObservable[Machine] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    page = params("page").asInstanceOf[KoObservable[String]]
    println("GOT A PAGE:")
    println(page())
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[Machine]]
  }

  override def template: String = {
    header(`class`:="toolbar toolbar-header",
      h1(`class`:="title", "Mondello"),
      div(`class`:="toolbar-actions",
        commonButtons(),
        machinesToolbar(),
        rightButtons()
      )
    ).toString()
  }

  // Template partials

  def commonButtons():Frag = {
    div(`class`:="btn-group",
      button(attrs.data.bind:="css: {active: page() == 'machines'}",
        `class`:="btn btn-default",
        span(`class`:="icon icon-drive"),
        raw("&nbsp; Machines")
      ),
      button(attrs.data.bind:="css: {active: page() === 'images'}",
        `class`:="btn btn-default",
        span(`class`:="icon icon-box"),
        raw("&nbsp; Images")
      ),
      button(attrs.data.bind:="css: {active: page() === 'containers'}",
        `class`:="btn btn-default",
        span(`class`:="icon icon-rocket"),
        raw("&nbsp; Containers")
      ),
      button(attrs.data.bind:="css: {active: page() === 'projects'}",
        `class`:="btn btn-default",
        span(`class`:="icon icon-pencil"),
        raw("&nbsp; Projects")
      )
    )
  }

  def machinesToolbar():Frag = {
    span(attrs.data.bind:="if: page()=='machines'",
      button(`class`:="btn btn-default",
        span(`class`:="icon icon-plus"), attrs.data.bind:="click: showNewMachine()",
        "New Machine"
      )
    )
  }

  def rightButtons():Frag = {
    span(
      button(`class`:="btn btn-default pull-right",
        span(`class`:="icon icon-cog"),
        raw("&nbsp; Settings")
      ),
      button(`class`:="btn btn-default pull-right",
        span(`class`:="icon icon-users"),
        raw("&nbsp; Credentials")
      )
    )
  }

  // callbacks

  def showNewMachine():js.Function1[js.Any,Unit] = {
    (evt:js.Any) => {
      println("* show new machine dialog")
    }
  }
}
