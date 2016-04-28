package knockout

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scalatags.Text.TypedTag


@JSExportAll
abstract class KoComponent {

  val tagName:String

  def viewModel(params:js.Dictionary[js.Any])

  def template:String

  val nestedComponents:scala.collection.mutable.Map[String,KoComponent] = scala.collection.mutable.Map()
  var parent:KoComponent = null

  def topLevel():KoComponent = {
    if(parent == null) {
      parent
    } else {
      parent.topLevel()
    }
  }
}

object KoComponent {
  def apply(component:KoComponent): Unit = {
    component.nestedComponents.foreach({ case ((_: String, child: KoComponent)) =>
      child.parent = component
      KoComponent(child)
    })

    val factoryFun:js.Function2[js.Dictionary[js.Any], js.Any, js.Any] = { (params:js.Dictionary[js.Any], config:js.Any) =>
      component.viewModel(params)
      component.asInstanceOf[js.Any]
    }
    val registrationInfo = js.Dictionary(
      "template" -> component.template,
      "viewModel" -> js.Dictionary(
        "createViewModel" -> factoryFun
    ))
    g.ko.components.register(component.tagName, registrationInfo)
  }

  def mkTag(tagName: String) = TypedTag[String](tagName, List(), void = false)
}