package controller.view

import controller.model.Project
import controller.MainApp
import scalafx.scene.control.{Alert, Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.StringProperty
import controller.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType

import scala.util.{Failure, Success}

@sfxml
class ProjectOverviewController(

                                 private val ProjectTable : TableView[Project],

                                 private val TaskColumn : TableColumn[Project, String],

                                 private val StatusColumn : TableColumn[Project, String],

                                 private val TaskLabel : Label,

                                 private val StatusLabel : Label,

                                 private val DescriptionLabel : Label,

                                 private val PriorityLabel :  Label,

                                 private val  DueDateLabel : Label

                               ) {

  ProjectTable.items = MainApp.projectData
  TaskColumn.cellValueFactory = {
    _.value.Task
  }
  StatusColumn.cellValueFactory = {
    _.value.Status
  }

  private def showProjectDetails(project: Option[Project]) = {
    project match {
      case Some(project) =>
        TaskLabel.text <== project.Task
        StatusLabel.text <== project.Status
        DescriptionLabel.text <== project.Description
        PriorityLabel.text <== project.Priority;
        DueDateLabel.text = project.DueDate.value.asString;

      case None =>
        TaskLabel.text.unbind()
        StatusLabel.text.unbind()
        DescriptionLabel.text.unbind()
        PriorityLabel.text.unbind()
        TaskLabel.text = ""
        StatusLabel.text = ""
        DescriptionLabel.text = ""
        PriorityLabel.text = ""
        DueDateLabel.text = ""
    }

  }

  showProjectDetails(None)

  ProjectTable.selectionModel.value.selectedItem.onChange((_, _, newValue) => {
    showProjectDetails(Option(newValue))
  })

  def handleDeleteProject(action: ActionEvent) = {
    val selectedIndex = ProjectTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      MainApp.projectData.remove(selectedIndex).delete() match{
        case Success(u) =>

        case Failure(e) =>
          new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title = "Fail to Remove"
            headerText = "Failure to remove Project"
            contentText = "Error occur Within Database."
          }.showAndWait()
      }
    } else {
      // Nothing selected.
      new Alert(AlertType.Information) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Project Selected"
        contentText = "Please select a project in the table."
      }.showAndWait()
    }
  }

  def handleNewProject(action: ActionEvent) = {
    val project = new Project("","")
    val okClicked = MainApp.showProjectEditDialog(project);
    if (okClicked) {
      MainApp.projectData += project
      project.save() match {
        case Success(u) =>
        case Failure(e) =>
      }

    }
  }

  def handleEditProject(action: ActionEvent) = {
    val selectedProject = ProjectTable.selectionModel().selectedItem.value
    if (selectedProject != null) {
      val okClicked = MainApp.showProjectEditDialog(selectedProject)

      if (okClicked) {
        showProjectDetails(Some(selectedProject))
        selectedProject.save() match {
          case Success(u) =>
          case Failure(e) =>

        }
      }
    } else {
      // Nothing selected.
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Project Selected"
        contentText = "Please select a project in the table."
      }.showAndWait()
    }

  }

}
