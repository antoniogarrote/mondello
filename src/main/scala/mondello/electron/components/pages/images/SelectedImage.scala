package mondello.electron.components.pages.images

import knockout.{KoComponent, KoObservable}
import mondello.models.Image

import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._
import scalatags.Text.{TypedTag, attrs}
import scala.scalajs.js.{Any, Dictionary}

@ScalaJSDefined
class SelectedImage extends KoComponent {
  override val tagName: String = SelectedImage.tagName

  var selectedImage:KoObservable[Image] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
  }

  override def template: String = {
    span(
      span(attrs.data.bind:="if: selectedImage()",
        imageHeader()
        /*,
        firstSection(),
        hashSection("Environment","environment"),
        hashSection("Driver","driverInfo"),
        hashSection("Engine","engineInfo"),
        hashSection("Swarm","swarmInfo"),
        hashSection("Authentication","authInfo")
        */
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

}

object SelectedImage {
  val tagName = "selected-image"
  val tag = KoComponent.mkTag(tagName)
}
