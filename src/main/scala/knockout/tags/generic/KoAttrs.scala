package knockout.tags.generic

import scalatags.generic._


trait KoAttrs[Builder, Output <: FragT, FragT] extends Util[Builder, Output, FragT]{
  val params       : Attr = "params".attr
}