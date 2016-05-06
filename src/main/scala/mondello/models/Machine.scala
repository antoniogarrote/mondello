package mondello.models

import java.util.Dictionary

import jdk.nashorn.api.scripting.JSObject
import mondello.proxies.NativeDocker

import scala.scalajs.js.annotation.{JSExportAll, ScalaJSDefined}
import scalajs.js

@JSExportAll
case class Machine(name:String,
                   active:String,
                   driver:String,
                   state:String,
                   url:String,
                   swarm:String,
                   env: Map[String,String],
                   inspect: js.Object) {

  def to_string() = s"docker-machine: $name $active $driver $state $url $swarm"
  def isNative() = name == NativeDocker.machineModel.name
}
