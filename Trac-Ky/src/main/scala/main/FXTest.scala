package main

import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.scene.paint.Color

import javafx.embed.swing.JFXPanel

import akka.actor._

object FXTest extends App {
  Application.launch(classOf[FXTest], args: _*)
}

import screen.PlayScreen
import akka.actor._

class FXTest extends Application {
  import com.typesafe.config._
  private val config = ConfigFactory.load()
  private val dX = config.getInt("display.x")
  private val dY = config.getInt("display.y")
  private val dW = config.getInt("display.w")
  private val dH = config.getInt("display.h")

  override def stop() = screen.close()

  val screen = new PlayScreen
  screen.showScreen()

  override def start(primaryStage: Stage): Unit = {
    implicit def eh(c: => Unit): EventHandler[ActionEvent] =
    new EventHandler[ActionEvent] {override def handle(event: ActionEvent):Unit= c}

    val btn_reload = new Button()
    btn_reload.setText("Reload")
    btn_reload.setOnAction(screen.reload())

    val btn_navigate = new Button()
    btn_navigate.setText("Navigate")
    btn_navigate.setOnAction(screen.reload())

    val root = new FlowPane()
    root.getChildren.addAll(btn_reload, btn_navigate)

    val scene = new Scene(root, 300, 250)
    primaryStage.setTitle("KtkrSazanami")
    primaryStage.setScene(scene)
    primaryStage.setResizable(false)
    primaryStage.setX(dX + dW)
    primaryStage.setY(dY)
    primaryStage.initStyle(StageStyle.TRANSPARENT)
    scene.setFill(Color.TRANSPARENT)
    primaryStage.show()
  }
}














































