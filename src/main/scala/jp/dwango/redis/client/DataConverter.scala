package jp.dwango.redis.client

import scala.util.matching.Regex

trait DataConverter {
  val name: String

  val keyMatcher: Regex

  def canConvert(key: String): Boolean =
    keyMatcher.pattern.matcher(key).matches

  def convert(value: String): String
}
