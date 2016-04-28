package mondello.proxies

import mondello.config.Environment
import mondello.models.{Container, Image}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}
import scalajs.js

class Docker(machineName:String, env:Environment)(implicit ec:ExecutionContext, consoleProcess: mondello.platform.Process) {
  implicit val currentEnv = env.copy(cmdPath = "eval $" + s"(${env.dockerMachinePath} env '$machineName'); ${env.dockerPath}")

  def containers:Future[List[Container]] = {
    val formatArg = "--format=\"{{.ID}}\\t{{.Image}}\\t{{.Command}}\\t{{.CreatedAt}}\\t{{.RunningFor}}\\t{{.Ports}}\\t{{.Status}}\\t{{.Size}}\\t{{.Names}}\\t{{.Labels}}\""
    consoleProcess.execute("ps",Array[String]("-a",formatArg)).map { (lines:Array[String]) =>
      lines.map(parseContainerLine(_)).toList
    }
  }

  def images: Future[List[Image]] = {
    consoleProcess.execute("images", Array[String]("--no-trunc")).map { (lines:Array[String]) =>
      lines.drop(1).map(parseImageLine(_))
    }.flatMap[List[Image]] { (images:Array[Image]) =>
      for {
        inspects <- this.inspect(images.map(_.id).toList)
      } yield {
        images.zip(inspects).map({
          case (image, inspection) =>
            val date = inspection.asInstanceOf[js.Dynamic].Created.asInstanceOf[String]
            val size = "" + inspection.asInstanceOf[js.Dynamic].VirtualSize
            image.copy(createdAt = date, size = parseSizeText(size), inspect = inspection)
        }).toList
      }
    }
  }

  def inspect(imageIds:List[String]): Future[List[js.Dynamic]] = {
    consoleProcess.execute("inspect", imageIds.toArray).map { (lines) =>
      val parsedInspect = js.JSON.parse(lines.mkString("")).asInstanceOf[js.Array[js.Dynamic]]
      val resultInspect = mutable.Buffer[js.Dynamic]()
      for(inspect <- parsedInspect){
        resultInspect.append(inspect)
      }
      resultInspect.toList
    }
  }

  def inspect(imageId:String): Future[js.Dynamic] = {
    val inspected = inspect(List[String](imageId))
    val result = Promise[js.Dynamic]()
    inspected.onComplete {
      case Success(inspectedList) => result.success(inspectedList.head)
      case Failure(e) => result.failure(e)
    }
    result.future
  }

  def startImageInteractive(id: String, command: String, opts: Map[String, String]):Future[Boolean] = {
    startImageInternal(interactive = true, id, command,opts, (args) => consoleProcess.executeInteractive("run", args.toArray))
  }

  def startImage(id: String, command: String, opts: Map[String, String]):Future[Boolean] = {
    startImageInternal(interactive = false, id, command,opts, (args) => consoleProcess.execute("run", args.toArray))
  }

  def stopContainer(id:String):Future[Boolean] = {
    consoleProcess.execute("stop", Array(id)).map((_) => true)
  }

  def destroyContainer(id:String):Future[Boolean] = {
    consoleProcess.execute("rm", Array("-f", id)).map((_) => true)
  }

  def startContainerInteractive(id:String):Future[Boolean] = {
    consoleProcess.executeInteractive("start", Array("-i", "-a", id)).map((_) => true)
  }

  def startContainer(id:String):Future[Boolean] = {
    consoleProcess.execute("start", Array(id)).map((_) => true)
  }

  def attachContainer(id:String):Future[Boolean] = {
    consoleProcess.executeInteractive("attach", Array("--sig-proxy", id)).map((_) => true)
  }

  protected def startImageInternal(interactive:Boolean, id: String, command: String, opts: Map[String, String], f:(List[String]) => Future[Array[String]]) = {
    val entrypointArg = makeCmdLineArg("entrypoint", opts("entrypoint"))
    val nameArg = makeCmdLineArg("name", opts("name"))
    val linkArg = makeCmdLineArg("link", opts("link"))
    val exposeArg = makeCmdLineArg("expose", opts("expose"))
    val publishArg = makeCmdLineArg("publish", opts("publish"))
    val envsArg = opts.getOrElse("env","").replace(",","\n").lines.map(_.replace("\n","").replace("\r","")).map(makeCmdLineArg("env",_))

    val interactiveArgs = if(interactive) List("--interactive=true", "--tty") else List[String]()
    val args =  interactiveArgs ++ List(entrypointArg, nameArg, linkArg, exposeArg, publishArg) ++ envsArg ++ List(id, command)
    f(args).map((_) => true)
  }
  protected def makeCmdLineArg(name:String, value:String): String = {
    if(value != null && value != "") {
      s"--$name=$value"
    } else {
      ""
    }
  }
  protected def parseImageLine(line: String): Image = {
    line.split("\\s+") match {
      case Array(repository, tag, id, _*) =>
        Image(repository, tag, id.replace("sha256:",""), null, 0L, null)
      case other =>
        throw new Exception(s"Impossible to parse array: $other")
    }
  }

  protected def parseSizeText(size: String): Long = java.lang.Long.parseLong(size)

  protected def parseContainerLine(line: String): Container = {
    line.split("\\t") match {
      case Array(id, image, command, createdAt, runningFor, ports, status, size, names, labels) =>
        val running = if(status.indexOf("Up") == 0) { true } else { false }
        new Container (
          id,
          image,
          command,
          createdAt,
          runningFor,
          status,
          running,
          parseMapLine(ports,"->"),
          names,
          parseMapLine(labels,"=")
        )
      case Array(id, image, command, createdAt, runningFor, ports, status, size, names) =>
        val running = if(status.indexOf("Up") == 0) { true } else { false }
        new Container (
          id,
          image,
          command,
          createdAt,
          runningFor,
          status,
          running,
          parseMapLine(ports,"->"),
          names,
          Map[String,String]()
        )
      case other =>
        throw new Exception(s"Impossible to parse array: $other")
    }
  }

  protected def parseMapLine(ports: String, separator: String): Map[String, String] = {
    ports.split("\\s*,\\s*").foldLeft(Map[String,String]()) { (ports, portLine) =>
      portLine.split(separator) match {
        case Array(left)      => ports + (left -> null)
        case Array(left,right) => ports + (left -> right)
      }
    }
  }
}
