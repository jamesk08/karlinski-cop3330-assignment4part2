/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TodoManager
{
    public ObservableList<Todo> getTodos(ArrayList<Todo> todos)
    {
        // convert given todo array list to options
        ObservableList<Todo> observableTodos = FXCollections.observableArrayList();
        for (Todo todo: todos)
        {
            observableTodos.add(todo);
        }

        // return observable todo objects
        return observableTodos;
    }

    public boolean validateTodoFields(
            String description,
            LocalDate dueDate,
            String formattedDate)
    {

        // initialize dialog manager
        DialogManager dialogManager = new DialogManager();

        // validate description input
        boolean isValid = true;
        if (description == null || description.trim().isEmpty())
        {
            dialogManager.displayError("Please enter description.", "Warning");
            isValid = false;
        }
        else if (description.length() > 256)
        {
            dialogManager.displayError("Description cannot be longer than 256 characters.", "Warning");
            isValid = false;
        }
        // validate due date input
        else if (dueDate == null)
        {
            dialogManager.displayError("Please enter due date.", "Warning");
            isValid = false;
        }
        else if (formattedDate.trim().isEmpty())
        {
            dialogManager.displayError("Please enter valid due date.", "Warning");
            isValid = false;
        }

        return isValid;
    }

    public void addTodo(
        TextField todoDescription,
        DatePicker todoDueDate,
        TodoList selectedTodoList,
        TableView<Todo> tblTodos)
    {
        // make sure a todo list is selected
        if (selectedTodoList.getTodoListId() == 0) return;

        // get description and due date input values
        String description = todoDescription.getText();
        LocalDate date = todoDueDate.getValue();

        String formattedDate = "";
        if (date != null) {
            formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // validate the description and due date input values
        if (validateTodoFields(description, date, formattedDate))
        {
            // generate the next todo id using todo list count
            int newTodoId = selectedTodoList.getTodos().size() + 1;

            // initialize new todo and add it to selected todo list
            Todo todo = new Todo(newTodoId, description, formattedDate);

            // add todos
            selectedTodoList.addTodos(todo);

            // get todo list file
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
                // save updated todo list from file storage
                FileOutputStream output = new FileOutputStream(todoListFile);
                ObjectOutputStream objOutput = new ObjectOutputStream(output);
                objOutput.writeObject(selectedTodoList);

                // clear input values
                todoDescription.clear();

                // reset default due date input
                todoDueDate.setValue(LocalDate.now().minusDays(-1));

                // refresh todo list using todos of selected list
                ObservableList<Todo> todos = getTodos(selectedTodoList.getTodos());
                tblTodos.setItems(todos);
            }
            catch (Exception e)
            {
                DialogManager dialogManager = new DialogManager();
                dialogManager.displayError(e.getMessage(), "Error");
            }
        }
    }

    public void deleteSelectedTodo(TodoList selectedTodoList, TableView<Todo> tblTodos)
    {
        // get selected todo item from todo display list
        Todo selectedTodo = tblTodos.getSelectionModel().getSelectedItem();

        // validate for selected todo
        if (selectedTodo == null)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select a todo to delete.", "Warning");
            return;
        }

        // find the todo list file using selected todo list
        TodoListManager todoListManager = new TodoListManager();
        File todoListFile = todoListManager.getTodoListFileById(selectedTodoList.getTodoListId());
        try
        {
            // just in case, create todo list file if doesn't exist
            todoListFile.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            // persist removal to the todo list file storage
            FileOutputStream output = new FileOutputStream(todoListFile);
            ObjectOutputStream objOutput = new ObjectOutputStream(output);
            objOutput.writeObject(selectedTodoList);

            // remove selected todo from todo list for the view table
            selectedTodoList.getTodos().remove(selectedTodo);
        }
        catch (Exception e)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayError(e.getMessage(), "Error");
        }
    }
}
