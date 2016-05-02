package mondello.electron.components.pages

import knockout._
import knockout.tags.KoText
import mondello.config.Log
import mondello.electron.components.MondelloApp
import mondello.electron.components.common.DockerBackendInteraction
import mondello.electron.components.pages.images.dialogs.{LaunchConfigurationDialog, PullImageDialog}
import mondello.electron.components.pages.images.{ImageFooter, ImagesBrowser, SelectedImage}
import mondello.models.{Image, ImageSearchResult}
import mondello.proxies.Docker

import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._

@JSExportAll
object Images extends KoComponent("docker-images") with DockerBackendInteraction {

  var docker:KoComputed[Docker] = null
  var images:KoObservableArray[Image] = Ko.observableArray()
  var selectedImage:KoObservable[Image] = Ko.observable(null)
  var loadingImages:KoObservable[Boolean] = Ko.observable(false)

  nestedComponents += (
    "imagesBrowser" -> ImagesBrowser,
    "images" -> SelectedImage,
    "imageFooter" -> ImageFooter
    )

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = params("docker").asInstanceOf[KoComputed[Docker]]
    this.docker.subscribe((_:Docker) => reloadImages())
  }

  override def template: String = {
    div(`class`:="window-content",
      div(`class`:="pane-group",
        ImagesBrowser.tag(`class`:="pane pane-sm sidebar",
          KoText.all.params:="loadingImages: loadingImages, selectedImage: selectedImage, images: images"),
        SelectedImage.tag(`class`:="pane padded-more",
          KoText.all.params:="selectedImage: selectedImage")
        ),
      LaunchConfigurationDialog.tag(),
      PullImageDialog.tag(),
      ImageFooter.tag(`class`:="toolbar-footer",
        KoText.all.params:="selectedImage: selectedImage")
    ).toString()
  }

  def reloadImages() = {
    Log.trace("*** Reloading Docker Images")
    loadingImages(true)
    if(docker() == null) {
      images.removeAll()
      val p = Promise[List[Image]]()
      p.success(List())
      loadingImages(false)
      p.future
    } else {
      val f = docker().images
      f.onSuccess {
        case newImages =>
          var foundSelectedImage = false
          images.removeAll()
          newImages.foreach({ (image: Image) =>
            if (selectedImage() != null && selectedImage().id == image.id) {
              foundSelectedImage = true
              selectedImage(image)
            }
            images.push(image)
          })
          if (!foundSelectedImage) selectedImage(null)
      }
      f.onFailure {
        case e => g.alert(s"!!! Error loading images $e")
      }
      // finished loading
      f.onComplete((_) => loadingImages(false))
      f
    }
  }

  def startImageInteractive(entrypoint:String, name:String, rm:Boolean, link:String, expose:String, publish:String, envs:String, command:String) = {
    Log.trace("** Start Image Interactive")
    startImageInternal(interactive = true, neverFail=false, rm, entrypoint, name,link, expose, publish, envs, command)
  }

  def startImage(entrypoint:String, name:String, rm:Boolean, link:String, expose:String, publish:String, envs:String, command:String) = {
    Log.trace("** Start Image")
    startImageInternal(interactive = false, neverFail=true, rm, entrypoint, name,link, expose, publish, envs, command)
  }

  def pullImage(image:String, tag:String):Future[Boolean] = {
    Log.trace(s"** Pulling image $image:$tag")
    dockerTry(docker()) {
      docker().pullImage(image,tag)
    }
  }

  def buildImage(dirname:String, tag:String, args:String, rm:Boolean):Future[Boolean] = {
    Log.trace(s"** Building image at path $dirname")
    dockerTry(docker()) {
      docker().buildimage(dirname, tag, args, rm)
    }
  }

  def destroyImage(image:Image):Future[Boolean] = {
    Log.trace(s"** Destroying image ${image.id}")
    val f = dockerTry(docker()) {
      MondelloApp.showModal(s"Destroying image ${image.repository}:${image.tag}")
      docker().destroyImage(image.id)
    }
    f.onSuccess {
      case (_) =>
        MondelloApp.closeModal()
        reloadImages()
    }
    f.onFailure {
      case e:Throwable =>
        g.alert(s"Error destroying image: ${e.getMessage}")
        MondelloApp.closeModal()
    }
    f
  }

  def searchImage(searchText:String):Future[Array[ImageSearchResult]] = {
    Log.trace(s"** Searching images: $searchText")
    dockerTry(docker(),Array[ImageSearchResult]()) {
      docker().searchImage(searchText)
    }
  }

  protected def startImageInternal(interactive:Boolean, neverFail:Boolean=false, rm:Boolean, entrypoint:String, name:String, link:String, expose:String, publish:String, envs:String, command:String) = {
    if(selectedImage() != null) {
      MondelloApp.showModal(s"Starting image ${this.selectedImage().repository}:${this.selectedImage().tag}")
      val id = selectedImage().id
      val opts = Map(
        "entrypoint" -> entrypoint,
        "name" -> name,
        "rm" -> rm.toString,
        "link" -> link,
        "expose" -> expose,
        "publish" -> publish,
        "env" -> envs
      )
      val f = if (interactive)
        docker().startImageInteractive(id, command, opts)
      else
        docker().startImage(id, command, opts)


      f.onSuccess {
        case result =>
          MondelloApp.closeModal()
      }

      f.onFailure {
        case e =>
          if(neverFail) {
            g.alert(s"The process returned an error, run it interactive for more info:\n\n${e.getMessage.substring(0,500)}${if(e.getMessage.length >500){"..."}}")
            MondelloApp.closeModal()
          } else {
            g.alert(e.getMessage)
            MondelloApp.closeModal()
          }
      }

      f
    } else {
      Promise[Boolean]().success(true).future
    }
  }
}