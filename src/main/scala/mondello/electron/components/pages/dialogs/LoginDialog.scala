package mondello.electron.components.pages.dialogs

import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.config.Log
import mondello.electron.components.MondelloApp
import mondello.models.Credential

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportAll
object LoginDialog extends KoComponent("login") {

  val index:KoObservable[String] = Ko.observable("")
  val username:KoObservable[String] = Ko.observable("")
  val password:KoObservable[String] = Ko.observable("")
  val credentials:KoObservableArray[Credential] = Ko.observableArray()
  var showLogin:KoObservable[Boolean] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    showLogin = params("showLogin").asInstanceOf[KoObservable[Boolean]]
    showLogin.subscribe { (loggedIn:Boolean) =>
      if(loggedIn) { loadCredentials() }
    }
  }

  override def template: String = {
    div(id:="login-dialog",`class`:="floating-window",
      attrs.data.bind:="visible: showLogin",
      header(`class`:="toolbar toolbar-header",
        h1(`class`:="title", "Index Credentials")
      ),
      div(`class`:="window-content",
        div(id:="logged-in",
          table(`class`:="table-striped",
            thead(
              th("Service"),
              th()
            ),
            tbody(id:="login-credentials-body",attrs.data.bind:="foreach: credentials",
              tr(
                td(attrs.data.bind:="text: $data.service"),
                td(
                  button(`type`:="submit",`class`:="bnt-cancel btn btn-form btn-default",
                    attrs.data.bind:="click: $parent.logout()",
                    span(`class`:="icon icon-logout")
                  )
                )
              )
            )
          )
        ),
        form(`class`:="padded-less",
          div(`class`:="form-group",
            label("Index"),
            input(id:="login-index", `class`:="form-control", placeholder:="https://index.docker.io/v1/", value:="https://index.docker.io/v1/",
              attrs.data.bind:="value: index")
          ),
          div(`class`:="form-group",
            label("Username"),
            input(id:="login-username", `class`:="form-control",
              attrs.data.bind:="value: username")
          ),
          div(`class`:="form-group",
            label("Password"),
            input(id:="login-password", `class`:="form-control", `type`:="password",
              attrs.data.bind:="value: password")
          ),
          form(`class`:="padded-less",
            div(`class`:="form-group",
              div(`class`:="form-actions",
                button(`type`:="submit",`class`:="btn-cancel btn btn-form btn-default pull-right",
                  attrs.data.bind:="click: hideLogin",
                  "Cancel"),
                button(`type`:="submit",`class`:="btn-ok btn btn-form btn-default pull-right",
                  attrs.data.bind:="click: okLogin",
                  "Ok")
              )
            )
          )
        )
      ),
      footer(`class`:="toolbar toolbar-footer")
    ).toString()
  }

  // Callbacks

  def okLogin() = {
    Log.trace(s"* ok login ->${username()}:${index()}<-")
    if(username() != "" && index() != "" && password() != "") {
      MondelloApp.showModal(s"Login into index as ${username()}")
      val f = MondelloApp.login(username(), index(), password())
      f.onSuccess {
        case res: Boolean =>
          loadCredentials()
          clear()
          MondelloApp.closeModal()
      }
      f.onFailure {
        case e: Throwable =>
          g.alert(s"Error login into service: ${e.getMessage}")
          MondelloApp.closeModal()
      }
    } else {
      g.alert("Please, provide full username, index and password")
    }
  }

  def hideLogin() = {
    Log.trace("* hide login")
    clear()
    showLogin(false)
  }

  def logout():KoCallback[Credential] = koCallback { (credential) =>
    Log.trace(s"* logout credential $credential")
    MondelloApp.showModal(s"Logging out from ${credential.service}")
    val f = MondelloApp.logout(credential.service)
    f.onSuccess {
      case res: Boolean =>
        loadCredentials()
        MondelloApp.closeModal()
    }
    f.onFailure {
      case e:Throwable =>
        g.alert(s"Error login out from service: ${e.getMessage}")
        MondelloApp.closeModal()
    }
  }

  // utility functions

  def loadCredentials() = {
    try {
      val homeEnvName = if (g.process.platform.asInstanceOf[String] == "win32") {
        "USERPROFILE"
      } else {
        "HOME"
      }
      val env = g.process.env.asInstanceOf[js.Dictionary[String]]
      val home = env(homeEnvName)

      val configPath = g.require("path").join(home, ".docker", "config.json")
      val accounts = g.JSON.parse(g.require("fs").readFileSync(configPath).toString()).auths.asInstanceOf[js.UndefOr[js.Dictionary[js.Dynamic]]]
      if (accounts.isDefined) {
        val found = accounts.get.keys.map { (service: String) =>
          Credential(
            service = service
          )
        }.toArray

        credentials.removeAll()
        for(credential <- found) {
          credentials.push(credential)
        }
      } else {
        Array[Credential]()
      }
    } catch {
      case e:Throwable =>
        Log.trace(s"** exception loading credentials $e")
        Array[Credential]()
    }
  }

  def clear() = {
    username("")
    index("")
    password("")
  }
}
