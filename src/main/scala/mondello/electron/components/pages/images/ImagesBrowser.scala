package mondello.electron.components.pages.images

import knockout.tags.KoText
import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.models.Image

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Any, Dictionary, JSON}
import scalatags.Text.all._
import scalatags.Text.attrs

@ScalaJSDefined
class ImagesBrowser extends KoComponent {
  override val tagName: String = ImagesBrowser.tagName

  var loadingImages:KoObservable[Boolean] = null
  var selectedImage:KoObservable[Image] = null
  var images:KoObservableArray[Image] = null
  var imageSearch:KoObservable[String] = Ko.observable("")
  var searchResults:KoObservableArray[Image] = Ko.observableArray()


  override def viewModel(params: Dictionary[Any]): Unit = {
    loadingImages = params("loadingImages").asInstanceOf[KoObservable[Boolean]]
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
    images = params("images").asInstanceOf[KoObservableArray[Image]]
    imageSearch.subscribe((searchText:String) => reloadSearchResults(searchText))
    images.subscribe((_:js.Array[Image]) => reloadSearchResults(imageSearch()))
    reloadSearchResults(imageSearch())
  }

  override def template: String = {
    ul(`class`:="list-group",
      li(`class`:="list-group-header",
        input(id:="imageSearchBox",`class`:="form-control", `type`:="text", placeholder:="Images Search",
          attrs.data.bind:="textInput: imageSearch"
        )
      ),
      // Not Images
      li(`class`:="list-group-item",
        attrs.data.bind:="if: loadingImages",
        div(`class`:="media-body",
          div(`class`:="spinner",
            div(`class`:="rect1"),
            div(`class`:="rect2"),
            div(`class`:="rect3"),
            div(`class`:="rect4"),
            div(`class`:="rect5")
          ))
      ),
      // Images
      raw("<!-- ko ifnot: loadingImages -->"),
      raw("<!-- ko foreach: searchResults -->"),
      /*
      h5("---"),
      div(attrs.data.bind:="text:$data"),
      div(attrs.data.bind:="text:$data.idSmall"),
      */
      li(`class`:="list-group-item",
        attrs.data.bind:="click: $parent.selectImage(),"++
          "css: {active: ($parent.selectedImage() && $parent.selectedImage().id == id)}",
        div(`class`:="media-body",
          span(`class`:="icon icon-box"),
          raw("&nbsp;"),
          strong(attrs.data.bind:="text: id"),
          p(attrs.data.bind:="text: repository")
        )
      ),
      raw("<!-- /ko -->"),
      raw("<!-- /ko -->")
    ).toString()
  }

  // Callbacks

  def selectImage():js.Function2[Image,js.Any,Unit] = {
    (image:Image, event:js.Any) => this.selectedImage(image)
  }

  // Helper Functions

  def reloadSearchResults(searchText:String): Unit = {
    searchResults.removeAll()
    if(images().length > 0) {
      images.slice(0, images().length).foreach { (image) =>
        if (
          image.id.indexOf(searchText) > -1 ||
          image.createdAt.indexOf(searchText) > -1 ||
          image.repository.indexOf(searchText) > -1 ||
          image.tag.indexOf(searchText) > -1
        ) {
          searchResults.push(image)
        }
      }
    }
  }

}

object ImagesBrowser {
  def tagName = "images-browser"
  def tag = KoComponent.mkTag(tagName)
}