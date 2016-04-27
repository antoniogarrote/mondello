package knockout

import scala.scalajs.js

@js.native
class KoObservable[T] extends js.Object {
  def apply(): T = js.native
  def apply(value: T): Unit = js.native
  def subscribe(f:js.Function1[T,Any], thisArg:Any = null, change:String = "change") = js.native
}