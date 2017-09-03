package main

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle
import scalafx.stage.StageStyle
import scalafx.scene.control.Button
import scalafx.scene.layout.FlowPane
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import screen.PlayScreen

object ScalaFXTest extends JFXApp {
  import com.typesafe.config._
  private val config = ConfigFactory.load()

  val screen = new PlayScreen
  screen.showScreen()

  stage = new JFXApp.PrimaryStage {
    title.value = "KtkrSazanami"
    x = config.getInt("display.x") + config.getInt("display.w")
    y = config.getInt("display.y")
    width = 300
    height = config.getInt("display.h")
    initStyle(StageStyle.TRANSPARENT)
    resizable = false
    scene = new ScreenControl(screen)
  }
}

class ScreenControl(screen: PlayScreen) extends Scene {
  fill = WHITE
  content = new FlowPane {
    children = Set(
        new Button {
          text = "reload"
          prefWidth = 100
          onMouseClicked = () => screen.reload()
        },
        new Button {
          text = "close"
        })
  }
}
