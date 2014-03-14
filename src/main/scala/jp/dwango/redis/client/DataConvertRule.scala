package jp.dwango.redis.client

import java.nio.ByteBuffer

sealed trait DataConvertRule {
  val name: String
  def convert(value: Array[Byte]): String
}

object BinaryConvertRule extends DataConvertRule {
  val name = "Binary"

  def convert(value: Array[Byte]): String = value.headOption.map("%02X".format(_)).getOrElse("")
}

object ShortConvertRule extends DataConvertRule {
  val name = "Short"

  def convert(value: Array[Byte]): String = ("%d".format(ByteBuffer.wrap(value).getShort))
}

object IntConvertRule extends DataConvertRule {
  val name = "Int"

  def convert(value: Array[Byte]): String = ("%d".format(ByteBuffer.wrap(value).getInt))
}

object LongConvertRule extends DataConvertRule {
  val name = "Long"

  def convert(value: Array[Byte]): String = ("%d".format(ByteBuffer.wrap(value).getLong))
}
