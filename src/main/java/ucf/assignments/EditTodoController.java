/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class EditTodoController implements Initializable
{
    @FXML private TextField editTodoDescription;
    @FXML private DatePicker todoDueDate;
    @FXML private ComboBox<String> comboStatusOptions;

    public Todo selectedTodo;
    public TodoList selectedTodoList;
    private MainController mainController;

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        // populate todo status option combo box
        ObservableList<String> statusOptions = FXCollections.observableArrayList("Incomplete", "Complete");
        comboStatusOptions.setItems(statusOptions);
    }

    public void setupFields(Todo selectedTodo, TodoList selectedTodoList)
    {
        // set input field values using the selected todo object
        this.selectedTodo = selectedTodo;
        this.selectedTodoList = selectedTodoList;
        if (selectedTodo != null)
        {
            editTodoDescription.setText(this.selectedTodo.getDescription());
            todoDueDate.setValue(LocalDate.parse(this.selectedTodo.getDueDate()));
            comboStatusOptions.setValue(this.selectedTodo.getStatus());
        }
    }

    public void setMainController(MainController mainController)
    {
        // set the parent controller so todo list can be refreshed after save
        this.mainController = mainController;
    }

    @FXML private void saveTodo(ActionEvent event) throws IOException
    {
        // make sure there a todo list is selected
        if (selectedTodoList != null && selectedTodoList.getTodoListId() > 0)
        {
            // map the selected status value
            int statusValue = comboStatusOptions.getSelectionModel().getSelectedIndex();
            if(statusValue < 0) statusValue = 0;
            String newStatus = statusValue == 0 ? "InComplete" : "Complete";

            // get description and due date input values
            String description = editTodoDescription.getText();
            LocalDate dueDate = todoDueDate.getValue();

            // format the due date to yyyy-MM-dd
            String formattedDate = "";
            if (dueDate != null)
            {
                formattedDate = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            // validate the description and due date input values
            var todoManager = new TodoManager();
            if (todoManager.validateTodoFields(description, dueDate, formattedDate))
            {
                // update valid todo values
                selectedTodoList.getTodos().get(selectedTodo.getTodoIndex()).setDescription(description);
                selectedTodoList.getTodos().get(selectedTodo.getTodoIndex()).setDueDate(formattedDate);
                selectedTodoList.getTodos().get(selectedTodo.getTodoIndex()).setStatus(newStatus);

                // find the path for the todo list file that contains the selected todo
                TodoListManager todoListManager = new TodoListManager();
                File todoListFile = todoListManager.getTodoListFileById(selectedTodoList.getTodoListId());
                todoListFile.createNewFile();

                // update the file
                FileOutputStream output = new FileOutputStream(todoListFile);
                ObjectOutputStream objOutput = new ObjectOutputStream(output);
                objOutput.writeObject(selectedTodoList);

                // refresh todo lists
                mainController.loadTodoLists(selectedTodoList.getTodoListIndex());

                // hide edit todo window
                ((Node)(event.getSource())).getScene().getWindow().hide();

                output.close();
            }
        }
        else
        {
            // warn user that a todo item was not selected
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select a todo list to edit.", "Warning");
        }
    }
}
