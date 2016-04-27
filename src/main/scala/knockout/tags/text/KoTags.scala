package knockout.tags.text

import scalatags.generic._
import scalatags.text._

import knockout.tags.generic

trait KoTags extends generic.KoTags[Builder, String, String]
  with generic.KoAttrs[Builder, String, String] {

  object polymer {
    val Component = "component".tag
  }
}
