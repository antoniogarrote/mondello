package mondello.proxies

import utest._
import mondello.config.Environment

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object DockerTest extends TestSuite {
  implicit object TestConsoleProcess extends mondello.platform.Process {
    override def execute(command: String, commandArgs: Array[String])(implicit environment: Environment): Future[Array[String]] = {
      val result = Promise[Array[String]]()
      val text = command match {
        case "images" => """REPOSITORY                                              TAG                 IMAGE ID                                                           CREATED             VIRTUAL SIZE
                       |<none>                                                  <none>              1eb489aef01029c3f040401ed85e7807563b7d2cf3af48511802847c74f3af2e   9 weeks ago         650.7 MB""".stripMargin
        case "inspect" => """[
                            |{
                            |    "Id": "1eb489aef01029c3f040401ed85e7807563b7d2cf3af48511802847c74f3af2e",
                            |    "RepoTags": [],
                            |    "RepoDigests": [],
                            |    "Parent": "2938064f4ac3c99be0e0592cf0fd7d433b160b6b38be8e79fffb62f08ce287b9",
                            |    "Comment": "",
                            |    "Created": "2016-02-08T21:26:38.695612711Z",
                            |    "Container": "4f2329eed0b5461e87af14e1e0a5f4568e7348d0e708863721fdd39b0f709677",
                            |    "ContainerConfig": {
                            |        "Hostname": "bc28b6258a7b",
                            |        "Domainname": "",
                            |        "User": "",
                            |        "AttachStdin": false,
                            |        "AttachStdout": false,
                            |        "AttachStderr": false,
                            |        "Tty": false,
                            |        "OpenStdin": false,
                            |        "StdinOnce": false,
                            |        "Env": [
                            |            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
                            |        ],
                            |        "Cmd": [
                            |            "/bin/sh",
                            |            "-c",
                            |            "git clone https://github.com/read-write-web/rww-play"
                            |        ],
                            |        "Image": "2938064f4ac3c99be0e0592cf0fd7d433b160b6b38be8e79fffb62f08ce287b9",
                            |        "Volumes": null,
                            |        "WorkingDir": "",
                            |        "Entrypoint": null,
                            |        "OnBuild": [],
                            |        "Labels": {}
                            |    },
                            |    "DockerVersion": "1.9.0",
                            |    "Author": "",
                            |    "Config": {
                            |        "Hostname": "bc28b6258a7b",
                            |        "Domainname": "",
                            |        "User": "",
                            |        "AttachStdin": false,
                            |        "AttachStdout": false,
                            |        "AttachStderr": false,
                            |        "Tty": false,
                            |        "OpenStdin": false,
                            |        "StdinOnce": false,
                            |        "Env": [
                            |            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
                            |        ],
                            |        "Cmd": [
                            |            "/bin/bash"
                            |        ],
                            |        "Image": "2938064f4ac3c99be0e0592cf0fd7d433b160b6b38be8e79fffb62f08ce287b9",
                            |        "Volumes": null,
                            |        "WorkingDir": "",
                            |        "Entrypoint": null,
                            |        "OnBuild": [],
                            |        "Labels": {}
                            |    },
                            |    "Architecture": "amd64",
                            |    "Os": "linux",
                            |    "Size": 8112311,
                            |    "VirtualSize": 650705393,
                            |    "GraphDriver": {
                            |        "Name": "aufs",
                            |        "Data": null
                            |    }
                            |}
                            |]""".stripMargin
        case "ps" => """74811a7e5a41	confluent/schema-registry	"/usr/local/bin/schem"	2016-04-16 12:58:38 +0100 BST	2 hours	0.0.0.0:8081->8081/tcp	Up About an hour	0 B	kafkacompose_schemaregistry_1com.docker.compose.config-hash=72da85e043d8475c70f474f4d531e66663a803b1568247fb9e9f1763f31a169b,com.docker.compose.container-number=1,com.docker.compose.oneoff=False,com.docker.compose.project=kafkacompose,com.docker.compose.service=schemaregistry,com.docker.compose.version=1.7.0
                       |68a7fd80124d	samsara/kafka	"/bin/sh -c /configur"	2016-04-16 12:58:38 +0100 BST	2 hours	0.0.0.0:9094->9094/tcp, 0.0.0.0:15004->15000/tcp	Up About an hour	0 B	kafkacompose_kafka3_1	com.docker.compose.container-number=1,com.docker.compose.oneoff=False,com.docker.compose.project=kafkacompose,com.docker.compose.service=kafka3,com.docker.compose.version=1.7.0,com.docker.compose.config-hash=d8d230b1bad7d2a7109b789fc4c9e80ea60e8905076a2f70555ffb0adbac9111
                       |0c4daa1c0ae7	samsara/kafka	"/bin/sh -c /configur"	2016-04-16 12:58:38 +0100 BST	2 hours	0.0.0.0:9093->9093/tcp, 0.0.0.0:15003->15000/tcp	Up About an hour	0 B	kafkacompose_kafka2_1	com.docker.compose.oneoff=False,com.docker.compose.project=kafkacompose,com.docker.compose.service=kafka2,com.docker.compose.version=1.7.0,com.docker.compose.config-hash=2b9c09336b0c2daec22e051bf9c4e061147ab184586717f5b8b05aac611e4a9d,com.docker.compose.container-number=1
                       |7a2aac4063f6	samsara/kafka	"/bin/sh -c /configur"	2016-04-16 12:58:38 +0100 BST	2 hours	0.0.0.0:9092->9092/tcp, 0.0.0.0:15002->15000/tcp	Up About an hour	0 B	kafkacompose_kafka1_1	com.docker.compose.config-hash=f34bcabeb3c62dc3d05a72541889668f8c803eb53dfa9ea10195859eef67d418,com.docker.compose.container-number=1,com.docker.compose.oneoff=False,com.docker.compose.project=kafkacompose,com.docker.compose.service=kafka1,com.docker.compose.version=1.7.0
                       |fc09913b21ab	samsara/zookeeper	"/bin/sh -c /configur"	2016-04-16 12:58:37 +0100 BST	2 hours	2888/tcp, 0.0.0.0:2181->2181/tcp, 3888/tcp, 0.0.0.0:15001->15000/tcp	Up About an hour	0 B	kafkacompose_zookeeper_1	com.docker.compose.project=kafkacompose,com.docker.compose.service=zookeeper,com.docker.compose.version=1.7.0,com.docker.compose.config-hash=bbbfa0c21f7f5dd73f2c94580350a61f0755d63d9a941f158ab137a829b13851,com.docker.compose.container-number=1,com.docker.compose.oneoff=False""".stripMargin
      }
      result.success(text.split("\n")).future
    }

    override def executeInteractive(command: String, commandArgs: Array[String])(implicit environment: Environment): Future[Array[String]] = ???
  }
  val tests = this {
    'images {
      val env = Environment.defaultEnv.copy(cmdPath = "testCmdPath")
      new Docker("machine1", env).images.map { (machines) =>
        assert(machines.length == 1)
        assert(machines.head.id == "1eb489aef01029c3f040401ed85e7807563b7d2cf3af48511802847c74f3af2e")
        assert(machines.head.createdAt == "2016-02-08T21:26:38.695612711Z")
      }
    }
    'ps {
      val env = Environment.defaultEnv.copy(cmdPath = "testCmdPath")
      new Docker("machine1", env).containers.map { (containers) =>
        assert(containers.length == 5)
        assert(containers.map(_.id) == List[String]("74811a7e5a41", "68a7fd80124d", "0c4daa1c0ae7", "7a2aac4063f6", "fc09913b21ab"))
      }
    }
  }
}
