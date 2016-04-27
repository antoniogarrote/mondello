package mondello.electron.components.pages

import knockout._
import knockout.tags.KoText
import mondello.electron.components.pages.images.{ImageFooter, ImagesBrowser, SelectedImage}
import mondello.models.Image
import mondello.proxies.Docker

import scala.concurrent.Promise
import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._
import scalatags.Text.attrs
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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
}