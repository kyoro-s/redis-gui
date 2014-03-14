package jp.dwango.redis.client

case class UIConfig(
    windowWidth: Int = 1024,
    windowHeight: Int = 768,
    splitWeight: Double = 0.35) {
  val title = "Redis GUI Client"
}
