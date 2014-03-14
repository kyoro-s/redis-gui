package jp.dwango.redis.client

import scala.io.Source

object ConfigProvider {
  val configs = {
    val seq = Source.fromFile("conf/redisui.conf").getLines.toSeq
    seq.flatMap { line =>
      line.split("=") match {
        case Array(key, value) =>
          val valueWithoutQuote = if (value.startsWith("\"") && value.endsWith("\"")) {
            value.substring(1, value.length - 1)
          } else value
          Some(key -> valueWithoutQuote)
        case _ => None
      }
    }.toMap
  }

  val redisClientConfig = {
    (0 to 32) flatMap { index =>
      val host = configs.get(s"redis.server.$index.host")
      host.map { h =>
        val port = configs.get(s"redis.server.$index.port")
        port.map ( p =>
          RedisClientConfig(h, p.toInt)
        ).getOrElse(RedisClientConfig(h))
      }
    }
  }

  val redisDataConfig = {
    val divider = configs.get("redis.key.divider")
    RedisDataConfig(divider.getOrElse("No Divider"))
  }

  val uiConfig = {
    val windowWidth = configs.get("window.width")
    val windowHeight = configs.get("window.height")
    val splitWeight = configs.get("window.split.weight")
    (windowWidth, windowHeight) match {
      case (Some(w), Some(h)) =>
        splitWeight.map( sw => UIConfig(w.toInt, h.toInt, sw.toDouble))
          .getOrElse(UIConfig(w.toInt, h.toInt))
      case _ =>
        splitWeight.map( sw => UIConfig(splitWeight = sw.toDouble))
          .getOrElse(UIConfig())
    }
  }

  val dataConverterConfigs = {
    Seq(DataConverterConfig("TestDataConverter", "syu.+", "Binary ,Binary , ..."))
  }
}
