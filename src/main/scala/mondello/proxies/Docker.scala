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
        images.zip(inspects).map({ (tuple) =>
          tuple match {
            case (image, inspection) => {
              val date = inspection.asInstanceOf[js.Dynamic].Created.asInstanceOf[String]
              val size = (""+inspection.asInstanceOf[js.Dynamic].VirtualSize)
              image.copy(createdAt = date, size = parseSizeText(size), inspect = inspection)
            }
          }
        }).toList
      }
    }
  }

  def inspect(imageIds:List[String]): Future[List[js.Object]] = {
    consoleProcess.execute("inspect", imageIds.toArray).map { (lines) =>
      val parsedInspect = js.JSON.parse(lines.mkString("")).asInstanceOf[js.Array[js.Object]]
      val resultInspect = mutable.Buffer[js.Object]()
      for(inspect <- parsedInspect){
        resultInspect.append(inspect)
      }
      resultInspect.toList
    }
  }

  def inspect(imageId:String): Future[js.Object] = {
    val inspected = inspect(List[String](imageId))
    val result = Promise[js.Object]()
    inspected.onComplete { (inspectResult) =>
      inspectResult match {
        case Success(inspectedList) => result.success(inspectedList.head)
        case Failure(e)             => result.failure(e)
      }
    }
    result.future
  }

  protected def parseImageLine(line: String): Image = {
    line.split("\\s+") match {
      case Array(repository, tag, id, _*) =>
        Image(repository, tag, id, null, 0L, null)
      case other => {
        throw new Exception(s"Impossible to parse array: $other")
      }
    }
  }

  protected def parseSizeText(size: String): Long = java.lang.Long.parseLong(size)

  protected def parseContainerLine(line: String): Container = {
    line.split("\\t") match {
      case Array(id, image, command, createdAt, runningFor, ports, status, size, names, labels) => {
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
      }
      case Array(id, image, command, createdAt, runningFor, ports, status, size, names) => {
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
      }
      case other => {
        throw new Exception(s"Impossible to parse array: $other")
      }
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
