package mondello.models

import scala.scalajs.js

case class Image(repository:String,
                 tag:String,
                 id:String,
                 createdAt:String,
                 size:Long,
                 inspect:js.Object) {

  val now:Long = new java.util.Date().getTime

  def idSmall:String = id.substring(0,12)

}

