package mondello.electron.components.pages.images.dialogs

import knockout.{Ko, KoComponent, KoObservable}
import mondello.config.Log
import mondello.electron.components.MondelloApp
import mondello.electron.components.pages.Images
import mondello.models.ImageSearchResult

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs


@JSExportAll
object SearchImageDialog extends KoComponent("search-image-dialog") {

  val imageText:KoObservable[String] = Ko.observable("")
  val searchResults = Ko.observableArray[ImageSearchResult]()
  val selectedResult = Ko.observable[ImageSearchResult](null)

  override def viewModel(params: Dictionary[Any]): Unit = {}

  override def template: String = {
    div(id:="search-image-dialog", `class`:="floating-window", style:="display: none",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Search Image")
      ),
      div(id:="images-results-window",`class`:="window-content",
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Image text"),
            input(id:="build-image-path",`class`:="form-control inline-form-input", attrs.data.bind:="value: imageText"),
            button(id:="build-image-path-btn",`class`:="btn btn-form btn-default pull-right inline-form-btn",
              attrs.data.bind:="click: startSearch",
              span(`class`:="icon icon-search")
            )
          )
        ),
        div(id:="images-selection-wrapper",
          table(id:="images-selection-table", `class`:="table-stripped",style:="margin-bottom: 0px",
            attrs.data.bind:="visible: searchResults().length > 0",
            thead(
              tr(
                th("Name", style:="width: 150px"),
                th("Description", style:="width:260px"),
                th("Stars"),
                th("Official"),
                th("Automated")
              )
            ),
            tbody(attrs.data.bind:="foreach: searchResults",
              tr(attrs.data.bind:="css: {'image-selected-result': ($parent.selectedResult() && $parent.selectedResult().name == $data.name)}," ++
                "click: $parent.selectResult()",
                td(attrs.data.bind:="text: name"),
                td(attrs.data.bind:="text: description",style:="width: 50px"),
                td(attrs.data.bind:="text: stars"),
                td(attrs.data.bind:="text: official"),
                td(attrs.data.bind:="text: automated")
              )
            )
          )
        ),
        form(style:="margin-bottom: 20px",
          div(`class`:="form-actions",
            button(`type`:="submit", `class`:="btn-cancel btn btn-form btn-default pull-right",
              attrs.data.bind:="click:cancelSearchImage",
              "Cancel"),
            button(`type`:="submit", `class`:="btn-ok btn btn-form btn-default pull-right",
              attrs.data.bind:="click:commitPullImage, css:{'btn-disabled': (selectedResult() == null)}",
              "Pull Image")
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // helper functions

  def show() = {
    imageText("")
    searchResults.removeAll()
    selectedResult(null)
    g.$("#search-image-dialog").show()
  }

  def hide() = {
    g.$("#search-image-dialog").hide()
  }

  // callbacks

  def startSearch() = {
    Log.trace("* Start search")
    if(imageText() != "") {
      MondelloApp.showModal("Searching images")
      val f = Images.searchImage(imageText())
      f.onSuccess { case (results:Array[ImageSearchResult]) =>
        searchResults.removeAll()
        for(result <- results) {
          searchResults.push(result)
        }
        MondelloApp.closeModal()
      }
      f.onFailure {
        case e:Throwable =>
          g.alert(s"Error search images: ${e.getMessage}")
          MondelloApp.closeModal()
      }
    }
  }

  def selectResult():KoCallback[ImageSearchResult] = koCallback(selectedResult(_))

  def cancelSearchImage() = {
    hide()
  }

  def commitPullImage() = {
    if(selectedResult() != null) {
      MondelloApp.showModal(s"Pulling image ${selectedResult().name}")
      val f = Images.pullImage(selectedResult().name,"")
      f.onSuccess {
        case _ =>
          MondelloApp.closeModal()
          hide()
          Images.reloadImages()
      }

      f.onFailure {
        case e:Throwable =>
          g.alert(s"Error pulling image: ${e.getMessage}")
      }
    } else {
      g.alert("Please, provide a name for the image")
    }
  }
}
