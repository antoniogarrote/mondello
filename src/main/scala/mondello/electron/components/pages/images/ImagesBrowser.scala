package mondello.electron.components.pages.images

import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.electron.components.common.SearchableList
import mondello.models.Image

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object ImagesBrowser extends KoComponent("images-browser") {

  var loadingImages:KoObservable[Boolean] = null
  var selectedImage:KoObservable[Image] = null
  var images:KoObservableArray[Image] = null

  val searchList = new SearchableList[Image] {
    override def isResult(image: Image, searchText: String): Boolean = {
      image.id.indexOf(searchText) > -1 ||
        image.createdAt.indexOf(searchText) > -1 ||
        image.repository.indexOf(searchText) > -1 ||
        image.tag.indexOf(searchText) > -1
    }
  }

  override def viewModel(params: Dictionary[Any]): Unit = {
    loadingImages = params("loadingImages").asInstanceOf[KoObservable[Boolean]]
    selectedImage = params("selectedImage").asInstanceOf[KoObservable[Image]]
    images = params("images").asInstanceOf[KoObservableArray[Image]]
    searchList.subscribe(images)
  }

  override def template: String = {
    ul(`class`:="list-group",attrs.data.bind:="css:{'list-group-with-elems':!loadingImages()}",
      li(`class`:="list-group-header",
        input(id:="imageSearchBox",`class`:="form-control", `type`:="text", placeholder:="Images Search",
          attrs.data.bind:="textInput: searchList.elementSearch"
        )
      ),
      // Not Images
      li(`class`:="list-group-item",
        attrs.data.bind:="visible: loadingImages()",
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
      raw("<!-- ko foreach: searchList.searchResults -->"),
      li(`class`:="list-group-item",
        attrs.data.bind:="click: $parent.selectImage(),"++
          "visible: !$parent.loadingImages(),"++
          "css: {active: ($parent.selectedImage() && $parent.selectedImage().id == id)}",
        div(`class`:="media-body",
          span(`class`:="icon icon-box"),
          raw("&nbsp;"),
          strong(attrs.data.bind:="text: idSmall"),
          p(attrs.data.bind:="text: repository")
        )
      ),
      raw("<!-- /ko -->")
    ).toString()
  }

  // Callbacks

  def selectImage():KoCallback[Image] = koCallback(this.selectedImage(_))
}