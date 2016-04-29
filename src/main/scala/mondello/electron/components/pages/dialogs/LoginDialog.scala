package mondello.electron.components.pages.dialogs

import knockout.{Ko, KoComponent, KoObservable, KoObservableArray}
import mondello.models.Credential

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._
import scalatags.Text.attrs

@JSExportAll
object LoginDialog extends KoComponent("login") {

  val index:KoObservable[String] = Ko.observable("")
  val email:KoObservable[String] = Ko.observable("")
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
              th("Email"),
              th()
            ),
            tbody(attrs.data.bind:="foreach: credentials",
              tr(
                td(attrs.data.bind:="text: $data.service"),
                td(attrs.data.bind:="text: $data.email"),
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
            input(id:="login-index", `class`:="form-control", placeholder:="latest", value:="https://index.docker.io/v1/",
              attrs.data.bind:="value: index")
          ),
          div(`class`:="form-group",
            label("Email"),
            input(id:="login-email", `class`:="form-control",
              attrs.data.bind:="value: email")
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
    println("* ok login")
  }

  def hideLogin() = {
    println("* hide login")
    showLogin(false)
  }

  def logout():KoCallback[Credential] = koCallback { (credential) =>
    println(s"* logout credential $credential")
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
            service = service,
            email = accounts.get(service).email.asInstanceOf[String]
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
        println(s"** exception loading credentials $e")
        Array[Credential]()
    }
  }
}
