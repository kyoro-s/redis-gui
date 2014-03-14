package jp.dwango.redis.client

sealed trait KeyType {
  val stringValue: String
}

case class NoneType() extends KeyType {
  val stringValue: String = "none"
}

object NoneType extends NoneType

case class StringType() extends KeyType {
  val stringValue: String = "string"
}

object StringType extends StringType

case class ListType() extends KeyType {
  val stringValue: String = "list"
}

object ListType extends ListType

case class SetType() extends KeyType {
  val stringValue: String = "set"
}

object SetType extends SetType

case class ZSetType() extends KeyType {
  val stringValue: String = "zset"
}

object ZSetType extends ZSetType

case class HashType() extends KeyType {
  val stringValue: String = "hash"
}

object HashType extends HashType

object KeyType {
  def apply(stringValue: String): KeyType = {
    stringValue match {
      case NoneType.stringValue => NoneType
      case StringType.stringValue => StringType
      case ListType.stringValue => ListType
      case SetType.stringValue => SetType
      case ZSetType.stringValue => ZSetType
      case HashType.stringValue => HashType
      case _ => throw new IllegalArgumentException
    }
  }
}