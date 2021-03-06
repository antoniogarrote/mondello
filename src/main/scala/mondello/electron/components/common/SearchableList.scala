package mondello.electron.components.common

import knockout.{Ko, KoObservable, KoObservableArray}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportAll}

trait SearchableList[T] {
  @JSExport
  var elementSearch:KoObservable[String] = Ko.observable("")
  @JSExport
  var searchResults:KoObservableArray[T] = Ko.observableArray()

  def reloadSearchResults(elements:KoObservableArray[T], searchText:String): Unit = {
    searchResults.removeAll()
    if(elements().length > 0) {
      elements.slice(0, elements().length).foreach { (element) =>
        if (isResult(element, searchText)) {
          searchResults.push(element)
        }
      }
    }
  }

  def subscribe(elements:KoObservableArray[T]) = {
    elements.subscribe((_:js.Array[T]) => reloadSearchResults(elements, elementSearch()))
    elementSearch.subscribe((searchText:String) => reloadSearchResults(elements, searchText))
    reloadSearchResults(elements, elementSearch())
  }

  def isResult(element:T,searchText:String):Boolean

  def subString(text:String)(f: => String) = {
    try {
      f.indexOf(text) > -1
    } catch {
      case _:Throwable => false
    }
  }

}
