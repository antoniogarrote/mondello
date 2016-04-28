package mondello.electron.components.common

import scalatags.Text.all._
import scalatags.Text.attrs

trait TableRenderer {
  def makeTable(tableName:String, base:String, attributesMapping:Seq[Tuple2[String,String]]): Frag = {
    div(`class`:="first-section",
      h4(tableName),
      table(`class`:="table-striped",
        thead(
          tr(
            th("Variable"),
            th("Value")
          )
        ),
        tbody(
          for((property, function) <- attributesMapping) yield tr(
            td(property),
            td(attrs.data.bind:=s"text: $base.$function")
          )
        )
      )
    )
  }

  def makeKeysTable(tableName:String, headerName:String, base:String): Frag = {
    div(`class`:="first-section",
      h4(tableName),
      table(`class`:="table-striped",
        thead(
          tr(
            th(headerName)
          )
        ),
        tbody(attrs.data.bind:=s"foreach: Object.keys($base || {})",
          tr(
            td(attrs.data.bind:="text: $data")
          )
        )
      )
    )
  }

  def makeKeyValueTable(tableName:String, headerName:(String,String), base:String): Frag = {
    div(`class`:="first-section",
      h4(tableName),
      table(`class`:="table-striped",
        thead(
          tr(
            th(headerName._1),
            th(headerName._2)
          )
        ),
        tbody(attrs.data.bind:=s"foreach: Object.keys($base || {})",
          tr(
            td(attrs.data.bind:="text: $data"),
            td(attrs.data.bind:="text: $parent."+base+"[$data]")
          )
        )
      )
    )
  }
}
