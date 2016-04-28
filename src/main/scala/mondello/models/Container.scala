package mondello.models

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class Container(id:String,
                     image:String,
                     command:String,
                     createdAt:String,
                     runningFor:String,
                     status:String,
                     running:Boolean,
                     ports:Map[String,String],
                     names:String,
                     labels:Map[String,String]) {

  val portsJs:js.Dictionary[String] = js.JSConverters.JSRichGenMap[String](ports).toJSDictionary
  val labelsJs:js.Dictionary[String] = js.JSConverters.JSRichGenMap[String](labels).toJSDictionary
}