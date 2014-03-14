package jp.dwango.redis.client

case class ResultTableRow(host: String, key: String, value: String, field: Option[String] = None, score: Option[Double] = None)
                         (implicit dataConverters: Seq[DataConverter]) {
  def toArray(): Array[AnyRef] = {
    val valueToShow = dataConverters.filter(_.canConvert(key)).headOption.map(c => s"${c.name} - ${c.convert(value)}").getOrElse(value)
    Array(host, key, field.getOrElse(""), valueToShow, score.map(_.toLong.toString).getOrElse(""))
  }

  def print() = {
    println(f"key:$key    value:$value    field:${field}    score:${score}")
  }
}

object ResultTableRowFactory {
  def getRows(key: KeyWithHostName, clients: RedisClients)
             (implicit dataConverters: Seq[DataConverter]): Seq[ResultTableRow] = {
    key.keyType match {
      case str: StringType =>
        clients.get(key) match {
          case Some(strValue) => Seq(ResultTableRow(key.host, key.key, strValue))
          case _ => Seq()
        }
      case hash: HashType =>
        clients.hgetAll(key) match {
          case Some(mapValue) =>
            mapValue.map {
              case (field, value) => ResultTableRow(key.host, key.key, value, Some(field))
            }.toSeq
          case _ => Seq()
        }
      case list: ListType =>
        clients.lrange(key, 0, -1) match {
          case Some(listValue) =>
            listValue.map(ResultTableRow(key.host, key.key, _))
          case _ => Seq()
        }
      case set: SetType =>
        clients.smembers(key) match {
          case Some(setValue) =>
            setValue.map(ResultTableRow(key.host, key.key, _)).toSeq
          case _ => Seq()
        }
      case zset: ZSetType =>
        clients.zrangeWithScores(key, 0, -1) match {
          case Some(setValue) =>
            setValue.map {
              case (value, score) => ResultTableRow(key.host, key.key, value, None, Some(score))
            }
          case _ => Seq()
        }
      case _ =>
        Seq()
    }
  }
}
