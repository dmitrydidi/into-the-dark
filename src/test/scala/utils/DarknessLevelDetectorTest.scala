package utils

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DarknessLevelDetectorTest
  extends AnyFunSuite
    with Matchers {

  test("toGrayScaleColor should be 1") {
    val res = utils.DarknessLevelDetector.toGrayScaleColor(1,2,3)
    assert(res == 1)
  }

}
