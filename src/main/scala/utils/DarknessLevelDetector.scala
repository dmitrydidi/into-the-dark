package utils
// java
import java.awt.Color
import java.awt.image.{BufferedImage, Raster}
import java.io.File
import javax.imageio.ImageIO
// scala
import scala.collection.parallel.CollectionConverters._
import scala.concurrent.{Future}
import scala.concurrent.ExecutionContext.Implicits.global

object DarknessLevelDetector {
  // using the basically formula for getting a 'GrayScale' image
  def toGrayScaleColor (red: Int, green: Int, blue: Int) = ((0.30 * red) + (0.59 * green) + (0.11 * blue)).toInt

  // converting inputted image to 'GrayScale' color
  def fromRGBAtoGray (image: BufferedImage): Future[BufferedImage] = Future {
    for { width <- (0 until image.getWidth).toVector
          height <- (0 until image.getHeight).toVector
          } yield {
      val color = image.getRGB(width, height)
      val red = (color & 0xff0000) / 65536
      val green = (color & 0xff00) / 256
      val blue = (color & 0xff)
      val grayColor = toGrayScaleColor(red, green, blue)
      image.setRGB(width, height, new Color(grayColor, grayColor, grayColor).getRGB)
    }
    image
  }
  // getting the average for compare with threshold
  def getAverage (image: BufferedImage): Future[Int] = Future {
    val raster: Raster = image.getRaster
    var reading: Double = 0.0
    for {height <- (0 until image.getHeight).toVector
         width <- (0 until image.getWidth).toVector
         } yield {
      reading += raster.getSample(width, height, 0)
    }
    val average = ((reading / (image.getHeight * image.getWidth * 256)) * 100).toInt
    average
}
  // here we just getting an images data
  def getImagesData (src: String): List[File] = {
    val sourceFile = new File(src)
    if (sourceFile.exists && sourceFile.isDirectory ) sourceFile.listFiles().filter( image =>
      image.isFile && (image.getName.endsWith(".png") || image.getName.endsWith(".jpg")) || image.getName.endsWith(".jpeg")).par.toList
    else List[File]()
  }
  // saving images to the output directory
  def saveImageToFolder (image: File, label: String, average: Int, output: String): Unit = {
    ImageIO
      .write(ImageIO
        .read(image), "jpg",
        new java.io.File(s"$output${image.getName.take(image.getName.lastIndexOf('.'))}$label${100 - average}.jpg"))
  }
}