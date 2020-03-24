// config
import pureconfig.generic.auto._
import pureconfig.error.ConfigReaderFailures
import pureconfig.ConfigSource
import config._
// utils
import utils.DarknessLevelDetector._
// java
import javax.imageio.ImageIO
// scala
import scala.collection.parallel.CollectionConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object Application extends App {

  //val config = ConfigFactory.load()
  val applicationConfig: Either[ConfigReaderFailures, Image] = ConfigSource.default.load[Image]

  applicationConfig match {
    case Left(ex) => ex.toList.foreach(println)
    case Right(config) =>
      val outputDirectory = config.image.output
      val images = getImagesData(config.image.input)
      images.map( image => {

        val photo = ImageIO.read(image)
        val convertedGrayPhoto = fromRGBAtoGray(photo)

        val awaitGray = Await.result(convertedGrayPhoto, Duration.Inf)
        val average = getAverage(awaitGray)

            average.onComplete {
              case Failure(error) => error
              case Success(averageValue) => if ((100 - averageValue > config.image.threshold))
                saveImageToFolder(image, "_dark_", averageValue, outputDirectory)
              else
                saveImageToFolder(image, "_bright_", averageValue, outputDirectory)
            }
      }).par
  }
}