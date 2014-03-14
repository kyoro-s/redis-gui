package jp.dwango.redis.client

import scala.util.matching.Regex

object DataConverterFactory {
  def create(config: DataConverterConfig): DataConverter = {
    new DataConverter {
      def convert(value: String): String =  {
        val bytes = value.getBytes("UTF-8")

        BinaryConvertRule.convert(bytes.slice(0, 1)) + " " +
          ShortConvertRule.convert(bytes.slice(1, 3)) + " " +
          new String(bytes.drop(3), "UTF-8")
      }

      val keyMatcher: Regex = new Regex(config.keyRegex)
      val name: String = config.name
    }
  }
}
