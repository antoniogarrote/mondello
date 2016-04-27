package knockout

import scala.scalajs.js

@js.native
class KoComputed[T] extends js.Object {
  def apply(f:()=>T) = js.native
}
