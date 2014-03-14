package jp.dwango.redis.client.ui.fx

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text
import scalafx.geometry.Insets

object RedisGUIApp extends JFXApp {
  stage = new PrimaryStage {
    title = "Redis GUI Client using ScalaFX"
    scene = new Scene {
      fill = YELLOW
      content = new HBox {
        padding = Insets(5)
        content = Seq(
          new Text {
            text = "TODO"
          }
        )
      }
    }
  }
}
