package knockout.tags.generic

import org.scalajs.dom
import scalatags.generic._

trait KoTags[Builder, Output <: FragT, FragT] extends Util[Builder, Output, FragT]  {
  trait polymer {
    val component: TypedTag[Builder, Output, FragT]
  }
}
