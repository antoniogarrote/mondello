package mondello.electron.components.pages.machines

import knockout._

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary, JSON}
import scalatags.Text.all._
import scalatags.Text.{TypedTag, attrs}

@ScalaJSDefined
class Machine extends KoComponent {
  override val tagName: String = Machine.tagName

  var selectedMachine:KoObservable[mondello.models.Machine] = null
  var environment:KoComputed[js.Array[js.Dictionary[String]]] = null
  var driverInfo:KoComputed[js.Array[js.Dictionary[String]]] = null
  var engineInfo:KoComputed[js.Array[js.Dictionary[String]]] = null
  var swarmInfo:KoComputed[js.Array[js.Dictionary[String]]] = null
  var authInfo:KoComputed[js.Array[js.Dictionary[String]]] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedMachine = params("selectedMachine").asInstanceOf[KoObservable[mondello.models.Machine]]
    environment = Ko.computed({ () =>
      val array = js.Array[js.Dictionary[String]]()
      if(selectedMachine() != null) {
        val machine:mondello.models.Machine = selectedMachine()
        if(machine.env != null) machine.env.foreach({
          case ((k: String, v: String)) => array.push(js.Dictionary[String]("key" -> k, "value" -> v))
        })
      }
      array
    })

    driverInfo = makeComputedHash(_.inspect.asInstanceOf[js.Dynamic].Driver)
    engineInfo = makeComputedHash(_.inspect.asInstanceOf[js.Dynamic].HostOptions.EngineOptions)
    swarmInfo = makeComputedHash(_.inspect.asInstanceOf[js.Dynamic].HostOptions.SwarmOptions)
    authInfo = makeComputedHash(_.inspect.asInstanceOf[js.Dynamic].HostOptions.AuthOptions)
  }

  override def template: String = {
    span(
      span(attrs.data.bind:="if: selectedMachine()",
        machineHeader(),
        firstSection(),
        hashSection("Environment","environment"),
        hashSection("Driver","driverInfo"),
        hashSection("Engine","engineInfo"),
        hashSection("Swarm","swarmInfo"),
        hashSection("Authentication","authInfo")
      ),
      span(attrs.data.bind:="ifnot: selectedMachine()",
        div(id:="hero-outer",
          div(id:="hero-inner",
            img(src:="images/mondello.png")
          )
        )
      )
    ).toString()
  }

  // helper functions

  private def makeComputedHash(f:(mondello.models.Machine) => js.Any):KoComputed[js.Array[js.Dictionary[String]]] = {
    Ko.computed({ () =>
      val array = js.Array[js.Dictionary[String]]()
      if(selectedMachine() != null) {
        val machine:mondello.models.Machine = selectedMachine()
        val data = f(machine)
        if(data != null) data.asInstanceOf[js.Dictionary[js.Dynamic]].foreach({
          case ((k: String, v: js.Dynamic)) => {
            v.constructor.name.asInstanceOf[String] match {
              case "Array" => array.push(js.Dictionary[String]("key" -> k, "value" -> v.join(",").asInstanceOf[String]))
              case "Object" => array.push(js.Dictionary[String]("key" -> k, "value" -> JSON.stringify(v)))
              case _        => array.push(js.Dictionary[String]("key" -> k, "value" -> v.toString()))
            }
          }
          case ((k:String, null)) => array.push(js.Dictionary[String]("key" -> k, "value" -> ""))
        })
      }
      array
    })
  }

  // templates

  private def machineHeader(): Frag = {
    div(`class`:="header-section",
      span(
        attrs.data.bind:="visible: selectedMachine().state === 'Running'",
        `class`:="icon icon-record",
        attrs.style:="color:#34c84a"),
      span(
        attrs.data.bind:="visible: selectedMachine().state === 'Saved'",
        `class`:="icon icon-record",
        attrs.style:="color:#fdbc40"),
      span(
        attrs.data.bind:="visible: selectedMachine().state !== 'Running' && selectedMachine().state !== 'Saved'",
        `class`:="icon icon-record",
        attrs.style:="color:#fc605b"),
      h2(attrs.data.bind:="text: selectedMachine().name")
    )
  }

  private def firstSection(): Frag = {
    div(`class`:="first-section",
      h4("Attributes"),
      div(`class`:="form-group",
        label(`class`:="form-group", "Status"),
        input(`class`:="form-control",
          attrs.data.bind:="value: selectedMachine().state")
      ),
      div(`class`:="form-group",
        label(`class`:="form-group", "Driver"),
        input(`class`:="form-control",
          attrs.data.bind:="value: selectedMachine().driver")
      ),
      div(`class`:="form-group",
        label(`class`:="form-group", "URL"),
        input(`class`:="form-control",
          attrs.data.bind:="value: selectedMachine().url")
      ),
      div(`class`:="form-group",
        label(`class`:="form-group", "Swarm"),
        input(`class`:="form-control",
          attrs.data.bind:="value: selectedMachine().swarm")
      )
    )
  }


  private def hashSection(sectionName:String, dataFunction:String): Frag = {
    div(`class`:="environment-section",
      h4(sectionName),
      table(`class`:="table-striped",
        thead(
          tr(
            th("Variable"),
            th("Value")
          )
        ),
        tbody(attrs.data.bind:=s"foreach: $dataFunction()",
          tr(
            td(attrs.data.bind:="text: key"),
            td(attrs.data.bind:="text: value")
          )
        )
      )
    )
  }
}

object Machine {
  val tagName:String = "machine"
  def tag:TypedTag[String] = KoComponent.mkTag(tagName)
}