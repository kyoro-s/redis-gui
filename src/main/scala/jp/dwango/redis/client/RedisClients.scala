package jp.dwango.redis.client

import com.redis.RedisClient

/**
 * 複数RedisインスタンスからRedisの操作を行うクラス
 *
 * @param configs 各Redisインスタンスの接続情報
 */
class RedisClients(configs: Seq[RedisClientConfig]) {
  val clients = configs.map(config => config.host -> new RedisClient(config.host, config.port)).toMap

  def keys(pattern: String): Seq[KeyWithHostName] = {
    clients.values.flatMap { client =>
      val l = client.keys(pattern).get.flatten
      l.map { key =>
        KeyWithHostName(client.host, key, keyType(client.host, key))
      }
    }.toSeq
  }

  def keyType(host: String, key: String): KeyType = {
    clients.get(host).flatMap { client =>
      client.getType(key).map(KeyType(_))
    }.getOrElse(NoneType)
  }

  def get(key: KeyWithHostName): Option[String] = {
    if (key.keyType == StringType) {
      clients.get(key.host).flatMap { client =>
        client.get(key.key)
      }
    } else {
      None
    }
  }

  def get(key: String): Option[String] = {
    clients.keys.flatMap(get(_, key)).headOption
  }

  def get(host: String, key: String): Option[String] = {
    clients.get(host).flatMap { client =>
      client.get(key)
    }
  }

  def lrange(key: KeyWithHostName, start: Int, end: Int): Option[List[String]] = {
    if (key.keyType == ListType) {
      clients.get(key.host).flatMap { client =>
        client.lrange(key.key, start, end).map(_.flatten)
      }
    } else {
      None
    }
  }

  def smembers(key: KeyWithHostName): Option[Set[String]] = {
    if (key.keyType == SetType) {
      clients.get(key.host).flatMap { client =>
        client.smembers(key.key).map(_.flatten)
      }
    } else {
      None
    }
  }

  def zrangeWithScores(key: KeyWithHostName, start: Int, end: Int): Option[List[(String, Double)]] = {
    if (key.keyType == ZSetType) {
      clients.get(key.host).flatMap { client =>
        client.zrangeWithScore(key.key, start, end)
      }
    } else {
      None
    }
  }

  def hgetAll(key: KeyWithHostName): Option[Map[String, String]] = {
    if (key.keyType == HashType) {
      clients.get(key.host).flatMap { client =>
        client.hgetall(key.key)
      }
    } else {
      None
    }
  }

  def hgetAll(host: String, key: String): Option[Map[String, String]] = {
    clients.get(host).flatMap { client =>
      client.hgetall(key)
    }
  }
}

