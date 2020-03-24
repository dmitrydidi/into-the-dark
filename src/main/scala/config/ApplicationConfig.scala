package config

import pureconfig.generic.auto._

case class Image(image: ImageDetails)

case class ImageDetails( input: String, output: String, threshold: Int )

//  case class AppConfig(image: ImageConfig)
//
//  case class ImageConfig(source: String, destination: String, threshold: Int)
//
//  object AppConfig {
//    def loadFromEnvironment(): AppConfig = load(ConfigUtil.loadFromEnvironment())
//
//  def load(config: Config): AppConfig =
//      AppConfig(
//        image = ImageConfig(
//          source = config.getString("image.source"),
//          destination = config.getString("image.destination"),
//          threshold  = config.getInt("image.threshold")
//        )
//      )
//  }
//
//  object ConfigUtil {
//    def loadFromEnvironment(): Config = {
//        ConfigFactory.load(System.getProperty(
//          "config.resource", "application.conf")))
//    }
//  }
//
//  class Image(config: ImageConfig){}

