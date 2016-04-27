package mondello.models

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
                     labels:Map[String,String])