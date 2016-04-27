package mondello.electron.components.pages.images

import knockout.{KoComponent, KoObservable}
import mondello.models.Image

import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@ScalaJSDefined
class ImageFooter extends KoComponent {
  override val tagName: String = ImageFooter.tagName

  var selectedImage:KoObservable[Image] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
  }

  override def template: String = {
    footer(`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: destroyImage, css: {'btn-disabled':!selectedImage()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Destroy Image"
        ),
        button(
          `class`:="btn btn-large btn-default",
          attrs.data.bind:="click: pullCurrentImage, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-cloud"),
          raw("&nbsp;"),
          "Pull"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: startImageDetached, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-play"),
          raw("&nbsp;"),
          "Launch Detached"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: startImageInteractive, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-monitor"),
          raw("&nbsp;"),
          "Launch Interactive"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          attrs.data.bind:="click: launchConfiguration, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-tools"),
          raw("&nbsp;"),
          "Launch Configuration"
        )
      )
    ).toString()
  }

  // Callbacks

  def destroyImage() = {
    println("** Destroy image")
  }

  def pullCurrentImage() = {
    println("** Pull Current Image")
  }

  def startImageDetached() = {
    println("** Start Image Detached")
  }

  def startImageInteractive() = {
    println("** Start Image Interactive")
  }

  def launchConfiguration() = {
    println("** Launch Configuration")
  }

}

object ImageFooter {
  val tagName = "image-footer"
  val tag = KoComponent.mkTag(tagName)
}