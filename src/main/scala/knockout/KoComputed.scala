package knockout

import scala.scalajs.js

@js.native
class KoComputed[T] extends KoObservable[T] {
  def apply(f:()=>T) = js.native
}
