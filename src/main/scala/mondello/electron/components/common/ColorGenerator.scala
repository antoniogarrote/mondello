package mondello.electron.components.common

import scala.util.Random

trait ColorGenerator {

  def hsvToRgb(h:Double, s:Double, v:Double):(Integer, Integer, Integer) = {
    val h_i = (h * 6).toInt
    val f = h * 6 - h_i
    val p = v * (1 - s)
    val q = v * (1 - f * s)
    val t = v * (1 - (1 - f) * s)
    val rgb = h_i match {
      case 0 => (v, t, p)
      case 1 => (q, v, p)
      case 2 => (p, v, t)
      case 3 => (p, q, v)
      case 4 => (t, p, v)
      case _ => (v, p, q)
    }
    ((rgb._1 * 256).toInt, (rgb._2 * 256).toInt, (rgb._3 * 256).toInt)
  }

  def htmlColor(rgb:(Integer,Integer,Integer)):String = {
    String.format("#%02x%02x%02x",rgb._1, rgb._2, rgb._3)
  }

  def nextColor():String = {
    val golden_ratio_conjugate = 0.618033988749895
    val h = Random.nextFloat()

    htmlColor(hsvToRgb(((h + golden_ratio_conjugate) % 1), 0.99, 0.99))
  }
}
