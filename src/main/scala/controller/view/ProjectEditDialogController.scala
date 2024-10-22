package controller.view

import controller.model.Project
import controller.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import controller.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class ProjectEditDialogController (

                                    private val  TaskField : TextField,
                                    private val   StatusField : TextField,
                                    private val     DescriptionField : TextField,
                                    private val       PriorityField : TextField,
                                    private val   DueDateField : TextField

                                  ){
  var         dialogStage : Stage  = null
  private var _project    : Project = null
  var         okClicked            = false

  def project: Project = _project
  def project_=(x : Project): Unit = {
    _project = x

    TaskField.text = _project.Task.value
    StatusField.text  = _project.Status.value
    DescriptionField.text    = _project.Description.value
    PriorityField.text      = _project.Priority.value
    DueDateField.text  = _project.DueDate.value.asString
    DueDateField.setPromptText("dd.mm.yyyy");
  }

  def handleOk(action :ActionEvent): Unit = {

    if (isInputValid()) {
      _project.Task <== TaskField.text
      _project.Status <== StatusField.text
      _project.Description    <== DescriptionField.text
      _project.Priority      <== PriorityField.text
      _project.DueDate.value       = DueDateField.text.value.parseLocalDate;

      okClicked = true;
      dialogStage.close()
    }
  }

  def handleCancel(action :ActionEvent): Unit = {
    dialogStage.close();
  }
  def nullChecking (x : String) = x == null || x.length == 0

  def isInputValid() : Boolean = {
    var errorMessage = ""

    if (nullChecking(TaskField.text.value))
      errorMessage += "No valid Task!\n"
    if (nullChecking(StatusField.text.value))
      errorMessage += "No valid Status!\n"
    if (nullChecking(DescriptionField.text.value))
      errorMessage += "No valid Description!\n"
    if (nullChecking(PriorityField.text.value))
      errorMessage += "No valid Priority!\n"
    if (nullChecking(DueDateField.text.value))
      errorMessage += "No valid Due Date!\n"
    else {
      if (!DueDateField.text.value.isValid) {
        errorMessage += "No valid birthday. Use the format dd.mm.yyyy!\n";
      }
    }

    if (errorMessage.length() == 0) {
      return true;
    } else {
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()

      return false;
    }
  }
}
