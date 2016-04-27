package knockout

import scala.scalajs.js
import scala.collection._

@js.native
class KoObservableArray[T] extends js.Object {
  def apply(index: Int): T = js.native
  def length:Int = js.native
  def indexOf(elem:T):Int = js.native
  def slice(from:Int, to:Int):mutable.Seq[T] = js.native
  def push(elem:T):Int = js.native
  def pop():T = js.native
  def shift():T = js.native
  def reverse():KoObservableArray[T] = js.native
  def sort():KoObservableArray[T] = js.native
  def sort(f:(T,T)=>Int):KoObservableArray[T] = js.native
  def splice(from:Int, to:Int): mutable.Seq[T] = js.native
  def remove(elem:T): mutable.Seq[T] = js.native
  def remove(f:(T) => Boolean): mutable.Seq[T] = js.native
  def removeAll(elems:Seq[T]): mutable.Seq[T] = js.native
  def removeAll(): mutable.Seq[T] = js.native
  def extend(options:mutable.Map[String,Any]): Unit = js.native
}