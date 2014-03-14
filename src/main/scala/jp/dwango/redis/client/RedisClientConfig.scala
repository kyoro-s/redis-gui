package jp.dwango.redis.client

/**
 * Redisの接続情報
 *
 * @param host
 * @param port
 */
case class RedisClientConfig(host: String = "localhost", port: Int = 6379)

case class RedisDataConfig(divider: String = ":")