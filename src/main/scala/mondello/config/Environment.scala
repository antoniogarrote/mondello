package mondello.config

case class Environment(cmdPath:String,
                       dockerMachinePath:String,
                       dockerPath:String,
                       dockerComposePath:String)

object Environment {
  def defaultEnv:Environment = Environment("", "docker-machine", "docker", "docker-compose")
}