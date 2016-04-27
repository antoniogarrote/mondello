package mondello.models

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class Image(repository:String,
                 tag:String,
                 id:String,
                 createdAt:String,
                 size:Long,
                 inspect:js.Dynamic) {

  val now:Long = new java.util.Date().getTime

  def idSmall:String = id.substring(0,12)

}

