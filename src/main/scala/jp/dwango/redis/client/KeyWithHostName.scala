package jp.dwango.redis.client

/**
 * Redisのキーとそのキーが格納されるホストの情報
 * @param host
 * @param key
 */
case class KeyWithHostName(host: String, key: String, keyType: KeyType, leafNum: Int = 0) {
  /**
   * ツリーのノードとして、Keyだけ表示するためにtoString()をオーバーライドする
   * @return
   */
  override def toString: String = if (leafNum > 0) s"$key($leafNum)" else s"$key"
}
