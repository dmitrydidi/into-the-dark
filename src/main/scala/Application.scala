import javax.imageio.ImageIO
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.DarknessLevelDetector._


object Application extends App {

  val config = ConfigFactory.load()
  val directoryIN = config.getString("image.input")
  val directoryOUT = config.getString("image.output")
  val threshold = config.getInt("image.threshold")

  implicit val akkaSystem = ActorSystem()

  val totalProcessingImages: Future[Int] = Source(getImageFiles(directoryIN))
    .map(f => getAverage(toGrayFromRGBA(ImageIO.read(f))) -> f)
    .map {
      case (averageValue, file) =>
        if (100 - averageValue > threshold)
          saveImage(file, "_dark_", averageValue, directoryOUT)
        else
          saveImage(file, "_bright_", averageValue, directoryOUT)
    }
    .runFold(0)((acc, elem) => if (elem) acc + 1 else acc)

  totalProcessingImages.onComplete { total =>
    total
      .map(t => println(s"total processing images is $t"))
      .failed
      .foreach(e => println(s"$e"))

    akkaSystem.terminate()
  }
}