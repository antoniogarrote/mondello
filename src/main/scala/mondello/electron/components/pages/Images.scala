package mondello.electron.components.pages

import knockout._
import knockout.tags.KoText
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.images.dialogs.LaunchConfigurationDialog
import mondello.electron.components.pages.images.{ImageFooter, ImagesBrowser, SelectedImage}
import mondello.models.Image
import mondello.proxies.Docker

import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary}
import scala.util.Try
import scalatags.Text.all._

@ScalaJSDefined
object Images extends KoComponent {
  override val tagName: String = "docker-images"
  val tag = KoComponent.mkTag(tagName)

  var docker:KoComputed[Docker] = null
  var images:KoObservableArray[Image] = Ko.observableArray()
  var selectedImage:KoObservable[Image] = Ko.observable(null)
  var loadingImages:KoObservable[Boolean] = Ko.observable(false)

  nestedComponents += (
    "imagesBrowser" -> new ImagesBrowser(),
    "images" -> new SelectedImage(),
    "imageFooter" -> new ImageFooter()
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
      ImageFooter.tag(`class`:="toolbar-footer",
        KoText.all.params:="selectedImage: selectedImage")
    ).toString()
  }

  def reloadImages() = {
    println("*** Reloading Docker Images")
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

  def startImageInteractive(entrypoint:String, name:String, link:String, expose:String, publish:String, envs:String, command:String) = {
    println("** Start Image Interactive")
    startImageInternal(interactive = true, entrypoint, name,link, expose, publish, envs, command)
  }

  def startImage(entrypoint:String, name:String, link:String, expose:String, publish:String, envs:String, command:String) = {
    println("** Start Image")
    startImageInternal(interactive = false, entrypoint, name,link, expose, publish, envs, command)
  }

  protected def startImageInternal(interactive:Boolean, entrypoint:String, name:String, link:String, expose:String, publish:String, envs:String, command:String) = {
    if(selectedImage() != null) {
      MondelloApp.showModal(s"Starting image ${this.selectedImage().repository}:${this.selectedImage().tag}")
      val id = selectedImage().id
      val opts = Map(
        "entrypoint" -> entrypoint,
        "name" -> name,
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
          g.alert(e.getMessage)
          MondelloApp.closeModal()
      }

      f
    } else {
      Promise[Boolean]().success(true).future
    }
  }
}