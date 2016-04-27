package mondello.models

import utest._

object ImageTest extends TestSuite {
  val tests = this {
    'idSmall {
      val repository = "repository"
      val tag = "tag"
      val id = "12345678901234567890"
      val createdAt = null
      val size = 0L

      val image1 = new Image(repository, tag, id, createdAt, size, null)
      assert(image1.idSmall == "123456789012")
    }
  }
}
