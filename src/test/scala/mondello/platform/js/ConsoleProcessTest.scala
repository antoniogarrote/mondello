package mondello.platform.js

import mondello.config.Environment
import utest._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ConsoleProcessTest extends TestSuite {
  val tests = this {
    'execute {
      implicit val env = Environment.defaultEnv.copy(cmdPath = "ls")
      Implicits.ConsoleProcess.execute("-al", Array[String]("src/test/resources/dir/*.txt")).map { (lines) =>
        assert(lines.length == 2)
      }
    }
  }
}
