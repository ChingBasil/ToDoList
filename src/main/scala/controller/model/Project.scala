package controller.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import java.time.LocalDate
import controller.util.Database
import controller.util.DateUtil._
import scalikejdbc._
import scala.util.{ Try, Success, Failure }

class Project (val TaskS : String, val StatusS : String) extends Database {
  def this()     = this(null, null)
  var Task  = new StringProperty(TaskS)
  var Status   = new StringProperty(StatusS)
  var Description     = new StringProperty("Task Description")
  var Priority      = new StringProperty("Low Priority")
  var DueDate       = ObjectProperty[LocalDate](LocalDate.of(2024, 8, 20))


  def save() : Try[Int] = {
    if (!(isExist)) {
      Try(DB autoCommit { implicit session =>
        sql"""
					insert into project (Task, Status,
						Description, Priority, DueDate) values
						(${Task.value}, ${Status.value}, ${Description.value},
						 ${Description.value}, ${DueDate.value.asString})
				""".update.apply()
      }).recover({
        case e : Exception =>
          e.printStackTrace()
          0
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
				update project
				set
				Task  = ${Task.value} ,
				Status   = ${Status.value},
				Description     = ${Description.value},
				Priority       = ${Priority.value},
				DueDate       = ${DueDate.value.asString}
				 where Task = ${Task.value} and
				 Status = ${Status.value}
				""".update.apply()
      })
    }

  }
  def delete() : Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
				delete from project where
					Task = ${Task.value} and Status = ${Status.value}
				""".update.apply()
      })
    } else
      throw new Exception("Project does not Exists in Database")
  }
  def isExist : Boolean =  {
    DB readOnly { implicit session =>
      sql"""
				select * from project where
				Task = ${Task.value} and Status = ${Status.value}
			""".map(rs => rs.string("Task")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }

  }
}
object Project extends Database{
  def apply (
              TaskS : String,
              StatusS : String,
              DescriptionS : String,
              PriorityS : String,
              DueDateS : String
            ) : Project = {

    new Project(TaskS, StatusS) {
      Description.value     = DescriptionS
      Priority.value       = PriorityS
      DueDate.value       = DueDateS.parseLocalDate
    }

  }
  def initializeTable(): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
			create table project (
			  id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
			  Task varchar(64),
			  Status varchar(64),
			  Description varchar(200),
			  Priority varchar(100),
			  DueDate varchar(64)
			)
			""".execute.apply()
    }
  }

  def getAllProjects : List[Project] = {
    DB readOnly { implicit session =>
      sql"select * from project".map(rs => Project(rs.string("Task"),
        rs.string("Status"),rs.string("Description"),
        rs.string("Priority"), rs.string("DueDate") )).list.apply()
    }
  }
}
