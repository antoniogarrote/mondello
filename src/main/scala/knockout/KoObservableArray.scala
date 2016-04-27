package knockout

import scala.scalajs.js
import scala.collection._

@js.native
class KoObservableArray[T] extends js.Object {
  def apply(): js.Array[T] = js.native
  def apply(index: Int): T = js.native
  def indexOf(elem:T):Int = js.native
  def slice(from:Int, to:Int):js.Array[T] = js.native
  def push(elem:T):Int = js.native
  def pop():T = js.native
  def shift():T = js.native
  def reverse():KoObservableArray[T] = js.native
  def sort():KoObservableArray[T] = js.native
  def sort(f:(T,T)=>Int):KoObservableArray[T] = js.native
  def splice(from:Int, to:Int): js.Array[T] = js.native
  def remove(elem:T): js.Array[T] = js.native
  def remove(f:(T) => Boolean): js.Array[T] = js.native
  def removeAll(elems:Seq[T]): js.Array[T] = js.native
  def removeAll(): js.Array[T] = js.native
  def extend(options:mutable.Map[String,Any]): Unit = js.native
  def subscribe(f:js.Function1[js.Array[T],Any], thisArg:Any = null, change:String = "change") = js.native
}