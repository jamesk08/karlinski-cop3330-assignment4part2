/*
 *  UCF COP3330 Fall 2021 Assignment 5 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class EditListController implements Initializable
{
    @FXML private TextField listTitle;

    public TodoList selectedTodoList = null;
    private MainController mainController;

    @Override public void initialize(URL url, ResourceBundle rb) { }

    public void setupFields(TodoList selectedTodoList)
    {
        // load selected todo list title field value
        this.selectedTodoList = selectedTodoList;
        if(selectedTodoList != null)
        {
            listTitle.setText(this.selectedTodoList.getTitle());
        }
    }

    public void setMainController(MainController mainController)
    {
        // set the parent controller so todo list can be refreshed after save
        this.mainController = mainController;
    }

    @FXML private void editList(ActionEvent event)
    {
        // make sure there is one selected todo list
        if (selectedTodoList.getTodoListId() >= 0)
        {
            String title = listTitle.getText();

            // make sure todo list title input is valid
            if (title.trim().isEmpty())
            {
                var dialogManager = new DialogManager();
                dialogManager.displayError("Please enter a title.", "Warning");
            }
            else
            {
                // set the title of the selected todo list
                selectedTodoList.setTitle(title);

                // get the path for the selected todo list file and initialize file
                TodoListManager todoListManager = new TodoListManager();
                File todoListFile = todoListManager.getTodoListFileById(selectedTodoList.getTodoListId());
                try
                {
                    todoListFile.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    // save the file with updated todo list title
                    FileOutputStream output = new FileOutputStream(todoListFile);
                    ObjectOutputStream objOutput = new ObjectOutputStream(output);
                    objOutput.writeObject(selectedTodoList);

                    // load todo list
                    mainController.loadTodoLists(selectedTodoList.getTodoListIndex());

                    // hide edit todo window
                    ((Node)(event.getSource())).getScene().getWindow().hide();

                    output.close();
                }
                catch (Exception e)
                {
                    var dialogManager = new DialogManager();
                    dialogManager.displayError(e.getMessage(), "Error");
                }
            }
        }
        else
        {
            // warn used if no todo list selection was made
            var dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select a list to edit.", "Warning");
        }
    }
}
