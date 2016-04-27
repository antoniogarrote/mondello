package knockout

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

import org.scalajs.dom

import scala.collection.mutable

@JSName("ko")
@js.native
object Ko extends js.Object {

  def observable[T](value: T): KoObservable[T] = js.native
  def computed[T](fn: js.Function0[T]): KoComputed[T] = js.native
  def observableArray[T](): KoObservableArray[T] = js.native
  def observableArray[T](values: mutable.Seq[T]): KoObservableArray[T] = js.native

  def applyBindings(viewModelOrBindingContext: Object): Unit = js.native
  def applyBindings(viewModelOrBindingContext: Object, rootNode: dom.Element): Unit = js.native
}