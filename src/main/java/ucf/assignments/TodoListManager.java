/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class TodoListManager
{
    public String todoListStoragePath = "src\\main\\java\\TodoLists";

    public TodoListManager() { }

    public TodoListManager(String todoListStoragePath)
    {
        this.todoListStoragePath = todoListStoragePath;
    }

    public ObservableList<TodoList> loadAllTodoListsFromStorage()
    {
        // initialize an array list to store todo lists that were loaded from files
        ArrayList<TodoList> todoLists = new ArrayList<>();

        // make sure there is a directory to store todo lists, create if doesn't exist
        File dir = new File(todoListStoragePath);
        if (!dir.isDirectory())
        {
            dir.mkdir();
        }

        // iterate all files in root todo list storage directory
        for (File file : Objects.requireNonNull(dir.listFiles()))
        {
            TodoList todoList = retrieveLists(file.getName());
            if (todoList != null)
            {
                todoLists.add(todoList);
            }
        }

        // return the data as observable list because TableView needs it
        ObservableList<TodoList> observableTodoLists = FXCollections.observableArrayList();
        observableTodoLists.addAll(todoLists);
        return observableTodoLists;
    }

    public File getTodoListFileByName(String fileName)
    {
        // returned a combined path of the root directory and file path
        String todoListFilePath = MessageFormat.format("{0}\\{1}", todoListStoragePath, fileName);
        return new File(todoListFilePath);
    }

    public File getTodoListFileById(int todoListId)
    {
        // return todo list path for given todo list
        String todoListPath = MessageFormat.format("{0}/{1}.txt", todoListStoragePath, todoListId);
        return new File(todoListPath);
    }

    public void addTodoList(String todoListTitle, ObservableList<TodoList> todoLists)
    {
        // create todo list storage directory if doesn't exist
        File dir = new File(todoListStoragePath);
        if (!dir.isDirectory()) dir.mkdir();

        if (todoListTitle.trim().isEmpty())
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please enter title for this todo list.", "Warning");
        }
        else
        {
            // generate the next todo list id
            int newTodoListId = findNextTodoListId(todoLists);

            // create new todo list object
            TodoList todoList = new TodoList(newTodoListId, todoListTitle);
            File todoListFile = getTodoListFileById(todoList.getTodoListId());
            try
            {
                // create new todo list file if doesn't exists
                todoListFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                // write into a file storage
                FileOutputStream output = new FileOutputStream(todoListFile);
                ObjectOutputStream objOutput = new ObjectOutputStream(output);
                objOutput.writeObject(todoList);

                output.close();
            }
            catch (Exception e)
            {
                DialogManager dialogManager = new DialogManager();
                dialogManager.displayError(e.getMessage(), "Error");
            }
        }
    }

    public void deleteSelectedTodoList(TodoList selectedTodoList, TableView<TodoList> tblTodoLists)
    {
        // validate for selected todo list
        if (selectedTodoList == null || selectedTodoList.getTodoListId() <= 0)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select todo list to delete.", "Warning");
            return;
        }

        // get the path for the todo list file that will be deleted
        File todoListFile = getTodoListFileById(selectedTodoList.getTodoListId());

        try
        {
            // delete todo list file from directory
            // Bug Note: deletion of file takes effect after application is stopped.
            boolean deleted = todoListFile.delete();

            // if todo list file is deleted remove the list item from display list
            if (deleted) tblTodoLists.getItems().removeAll(selectedTodoList);
        }
        catch (Exception e)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayError(e.getMessage(), "Error");
        }
    }

    public int findNextTodoListId(ObservableList<TodoList> todoLists)
    {
        // if view table is empty it means this is first item
        if (todoLists == null || todoLists.size() == 0) return 1;
        else
        {
            // get the ID of latest todo list
            TodoList todoList = todoLists.get(todoLists.size() - 1);
            return todoList.getTodoListId() + 1;
        }
    }

    private TodoList retrieveLists(String filename)
    {
        // get file path and initialize file object
        TodoList todoList = null;
        File todoListFileName = getTodoListFileByName(filename);

        try
        {
            // read file and initialize a new todo object using file data
            FileInputStream output = new FileInputStream(todoListFileName);
            ObjectInputStream objInput = new ObjectInputStream(output);
            todoList = (TodoList) objInput.readObject();

            // close the reader
            objInput.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // return the todo list object
        return todoList;
    }
}