package mondello.proxies

import mondello.config.Environment
import mondello.models.Project

import scala.concurrent.{ExecutionContext, Future}

class DockerCompose(machineName:String, env:Environment)(implicit ec:ExecutionContext, consoleProcess: mondello.platform.Process) {

  implicit val currentEnv = env.copy(cmdPath = "eval $" + s"(${env.dockerMachinePath} env '$machineName'); ${env.dockerComposePath}")

  def upServices(detached:Boolean, project:Project, services:Array[String]): Future[Boolean] = {
    if(detached){
      consoleProcess.execute(s"-f ${project.file } up", services).map((_) => true)
    } else {
      consoleProcess.executeInteractive(s"-f ${project.file} up", services).map((_) => true)
    }
  }

  def stopServices(project:Project, services:Array[String]): Future[Boolean] = {
    consoleProcess.executeInteractive(s"-f ${project.file} stop", services).map((_) => true)
  }

  def down(project:Project): Future[Boolean] = {
    consoleProcess.executeInteractive(s"-f ${project.file} down", Array[String]("--rmi all -v --remove-orphans")).map((_) => true)
  }

}
