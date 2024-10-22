package controller.view
import controller.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController(){
  def getStarted(): Unit = {
    MainApp.showProjectOverview()

  }

}