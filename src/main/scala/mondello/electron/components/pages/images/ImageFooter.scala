package mondello.electron.components.pages.images

import knockout.{KoComponent, KoObservable}
import mondello.electron.components.pages.images.dialogs.{LaunchConfigurationDialog, PullImageDialog}
import mondello.models.Image
import mondello.electron.components.pages.Images

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ImageFooter extends KoComponent("image-footer") {

  var selectedImage:KoObservable[Image] = null

  nestedComponents += (
    "LaunchConfigurationDialog" -> LaunchConfigurationDialog
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
  }

  override def template: String = {
    footer(`class`:="toolbar toolbar-footer",
      div(`class`:="toolbar-actions",
        button(
          `class`:="btn btn-large btn-default",
          title:="Destroys the selected Docker image",
          attrs.data.bind:="click: destroyImage, css: {'btn-disabled':!selectedImage()}",
          span(`class`:="icon icon-trash"),
          raw("&nbsp;"),
          "Destroy Image"
        ),
        button(
          `class`:="btn btn-large btn-default",
          title:="Pulls changes for the selected image from the Docker index",
          attrs.data.bind:="click: pullCurrentImage, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-cloud"),
          raw("&nbsp;"),
          "Pull"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Starts a container for the selected image and run configuration in the background",
          attrs.data.bind:="click: startImageDetached, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-play"),
          raw("&nbsp;"),
          "Launch Detached"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Starts a container for the selected image and run configuration attached",
          attrs.data.bind:="click: startImageInteractive, css: {'btn-disabled': !selectedImage()}",
          span(`class`:="icon icon-monitor"),
          raw("&nbsp;"),
          "Launch Interactive"
        ),
        button(
          `class`:="btn btn-large btn-default pull-right",
          title:="Container launch settings",
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
    Images.destroyImage(selectedImage())
  }

  def pullCurrentImage() = {
    println("** Pull Current Image")
    Images.pullImage(selectedImage().repository, selectedImage().tag)
  }

  def startImageDetached() = {
    println("** Start Image Detached")
    if(selectedImage != null) {
      println("** Start Image Interactive")
      Images.startImage(
        LaunchConfigurationDialog.entryPoint(),
        LaunchConfigurationDialog.name(),
        LaunchConfigurationDialog.link(),
        LaunchConfigurationDialog.expose(),
        LaunchConfigurationDialog.publish(),
        LaunchConfigurationDialog.envVars(),
        LaunchConfigurationDialog.command()
      )
    }
  }

  def startImageInteractive() = {
    if(selectedImage != null) {
      println("** Start Image Interactive")
      Images.startImageInteractive(
        LaunchConfigurationDialog.entryPoint(),
        LaunchConfigurationDialog.name(),
        LaunchConfigurationDialog.link(),
        LaunchConfigurationDialog.expose(),
        LaunchConfigurationDialog.publish(),
        LaunchConfigurationDialog.envVars(),
        LaunchConfigurationDialog.command()
      )
    }
  }

  def launchConfiguration() = {
    println("** Launch Configuration")
    LaunchConfigurationDialog.show()
  }
}