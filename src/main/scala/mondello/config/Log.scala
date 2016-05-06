package mondello.config

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

object Log {
  def isDev = g.process.env.asInstanceOf[js.Dictionary[String]].get("ENV").isDefined &&
    g.process.env.asInstanceOf[js.Dictionary[String]].get("ENV").get == "development"


  def trace(m:Any) = if(isDev){ g.console.log(m.toString) }
}
