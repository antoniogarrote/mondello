package mondello.electron.components.pages

import knockout.{KoComponent, KoComputed}
import mondello.proxies.Docker

import scala.scalajs.js.{Any, Dictionary}
import scala.scalajs.js.annotation.ScalaJSDefined

import scalatags.Text.all._
import scalatags.Text.attrs


@ScalaJSDefined
object Images extends KoComponent {
  override val tagName: String = "docker-images"
  val tag = KoComponent.mkTag(tagName)

  var docker:KoComputed[Docker] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    this.docker = params("docker").asInstanceOf[KoComputed[Docker]]
  }

  override def template: String = {
    span(
      h1("IMAGES").toString(),
      h2(attrs.data.bind:="text: docker()")
    ).toString()
  }
}