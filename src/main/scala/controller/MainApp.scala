package controller
import controller.model.Project
import controller.util.Database
import controller.view.ProjectEditDialogController
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp {

  Database.setupDB()

  val projectData = new ObservableBuffer[Project]()

  projectData ++= Project.getAllProjects

  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load();
  val roots : javafx.scene.layout.BorderPane = loader.getRoot[jfxs.layout.BorderPane]
  stage = new PrimaryStage {
    title = "Todo List"
    icons += new Image("logo.jpg")
    scene = new Scene {
      stylesheets += getClass.getResource("view/DarkThemed.css").toString
      root = roots
    }
  }
  // actions for display person overview window
  def showProjectOverview() = {
    val resource = getClass.getResource("view/ProjectOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showWelcome() = {
    val resource = getClass.getResource("view/Welcome.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showProjectEditDialog(project: Project): Boolean = {
    val resource = getClass.getResourceAsStream("view/ProjectEditDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[ProjectEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      title = "Edit Project"
      icons += new Image("logo.jpg")
      scene = new Scene {
        stylesheets += getClass.getResource("view/DarkThemed.css").toString
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.project = project
    dialog.showAndWait()
    control.okClicked
  }

  showWelcome()

}
