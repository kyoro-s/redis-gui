package jp.dwango.redis.client.ui.swing

import jp.dwango.redis.client.ConfigProvider
import scala.swing._

object RedisGUIApp extends SimpleSwingApplication {

  def top = new MainFrame {
    // Windowのタイトル
    title = ConfigProvider.uiConfig.title

    // Windowのサイズ
    minimumSize = new Dimension(ConfigProvider.uiConfig.windowWidth, ConfigProvider.uiConfig.windowHeight)
    contents = new RedisPanel
  }
}

