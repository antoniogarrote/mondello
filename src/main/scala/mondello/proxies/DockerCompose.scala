package mondello.proxies

import mondello.config.Environment

import scala.concurrent.ExecutionContext

class DockerCompose(machineName:String, env:Environment)(implicit ec:ExecutionContext, consoleProcess: mondello.platform.Process) {

  implicit val currentEnv = env.copy(cmdPath = "eval $" + s"(${env.dockerMachinePath} env '$machineName'); ${env.dockerComposePath}")

}
