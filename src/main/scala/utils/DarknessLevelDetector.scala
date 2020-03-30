package utils

import java.awt.Color
import java.awt.image.{BufferedImage, Raster}
import java.io.{File, IOException}
import javax.imageio.ImageIO

import scala.util.Try

object DarknessLevelDetector {
  // using the basically formula for getting a 'GrayScale' image
  def toGrayScaleColor (red: Int, green: Int, blue: Int) = ((0.30 * red) + (0.59 * green) + (0.11 * blue)).toInt

  // converting inputted image to 'GrayScale' color
  def toGrayFromRGBA(image: BufferedImage): BufferedImage = {
    for {
      width <- 0 until image.getWidth
      height <- 0 until image.getHeight
    } {
      val color = image.getRGB(width, height)
      val red = (color & 0xff0000) / 65536
      val green = (color & 0xff00) / 256
      val blue = color & 0xff
      val grayColor = toGrayScaleColor(red, green, blue)
      image.setRGB(width, height, new Color(grayColor, grayColor, grayColor).getRGB)
    }

    image
  }
  // getting the average for compare with threshold
  def getAverage (image: BufferedImage): Int= {
    val raster: Raster = image.getRaster
    val reading: Double =
      (for {
        height <- 0 until image.getHeight
        width <- 0 until image.getWidth
      } yield (width, height)).foldLeft(0.0) {
        case (acc, (w, h)) => acc + raster.getSample(w, h, 0)
      }
    (100 * reading / (image.getHeight * image.getWidth * 256)).toInt
}
  // here we just getting an images data
  def getImageFiles(src: String): List[File] =
    Try(new File(src)).toOption.map {
      sourceFile =>
        if (sourceFile.exists && sourceFile.isDirectory)
          sourceFile.listFiles().filter(image =>
            image.isFile && (image.getName.endsWith(".png") || image.getName.endsWith(".jpg")) || image.getName.endsWith(".jpeg")).toList
        else Nil
    }.getOrElse(Nil)

  // saving images to the output directory
  def saveImage(image: File, label: String, average: Int, output: String): Boolean =
    try {
      ImageIO.write(ImageIO.read(image), "jpg",
        new File(s"$output${image.getName.take(image.getName.lastIndexOf('.'))}$label${100 - average}.jpg"))
      true
    } catch {
      case _:IllegalArgumentException => false
      case _:IOException => false
    }
}