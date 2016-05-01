package mondello.electron

import scala.scalajs.js
import js.Dynamic.{global => g}
import io.atom.electron._
import mondello.electron.components.common.FileLoader

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.timers._

object Main extends js.JSApp with FileLoader {

  val mondelloSettingsPath = g.require("path").join(g.require("app").getPath("appData"), "mondello.json").toString

  def bootstrap(app:App) = {

    val fs = g.require("fs")
    val f = loadFile(mondelloSettingsPath)

    f.onSuccess {
      case (data: String) =>
        var parsed = g.JSON.parse(data).asInstanceOf[js.Dictionary[js.Any]]
        println("* Found settings")
        println(data)
        val dockerHome = parsed("dockerHome").asInstanceOf[String]
        val driversHome = parsed("driversHome").asInstanceOf[String]
        println("* Setting the path")
        val origPath = g.process.env.asInstanceOf[js.Dictionary[js.Any]]("PATH")
        g.process.env.asInstanceOf[js.Dictionary[js.Any]].update("PATH",origPath+":"+dockerHome+":"+driversHome)
    }

    null
  }

  def main(): Unit = {

    println(s"** Mondello main process, path: $mondelloSettingsPath")

    val app = g.require("app").asInstanceOf[App]  // Module to control application life.
    bootstrap(app)

    // Keep a global reference of the window object, if you don't, the window will
    // be closed automatically when the JavaScript object is GCed.
    var mainWindow: BrowserWindow = null

    // Report crashes to our server.
    g.require("crash-reporter").start()

    Messages.subscribeMondelloSettingsPath(mondelloSettingsPath)

    // Quit when all windows are closed.
    app.on("window-all-closed", { () =>
      // On OS X it is common for applications and their menu bar
      // to stay active until the user quits explicitly with Cmd + Q
      if (Process.platform != "darwin") {
        app.quit()
      }
    })

    // This method will be called when Electron has finished
    // initialization and is ready to create browser windows.
    app.on("ready", () => {
      // Create the browser window.
      mainWindow = BrowserWindow(
        width = 1200,
        height = 800
      )

      // and load the index.html of the app.
      mainWindow.loadURL("file://" + g.__dirname + "/index.html")

      // Open the devtools.
      mainWindow.openDevTools()

      // Emitted when the window is closed.
      val _ = mainWindow.on("closed", () =>
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        mainWindow = null
      )
    })
  }
}
