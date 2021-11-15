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
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController implements Initializable
{
    private static final String TODO_CONTROLLER_RESOURCE_PATH = "EditTodo.fxml";
    private static final String TODO_LIST_CONTROLLER_RESOURCE_PATH = "EditTodoList.fxml";

    @FXML private EditTodoController editTodoController;
    @FXML private EditListController editTodoListController;

    // todo list table and column
    private TodoList selectedTodoList;
    @FXML private TableView<TodoList> tblTodoLists;
    @FXML TableColumn<TodoList, String> title;

    // add todo fields
    @FXML private TextField todoListTitle;
    @FXML private TextField todoDescription;
    @FXML private DatePicker todoDueDate;

    // todo table and columns
    @FXML private TableView<Todo> tblTodos;
    @FXML TableColumn<TodoList, String> description;
    @FXML TableColumn<TodoList, String> dueDate;
    @FXML TableColumn<TodoList, String> status;

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        // load todo list from storage and make the first item selected
        loadTodoLists(0);

        //  setup table columns
        setupTodoListTable();
        setupTodoTable();

        // set default value for todo due date
        todoDueDate.setValue(LocalDate.now().minusDays(-1));
    }

    @FXML public void loadTodoLists(int rowIndexToSelect)
    {
        // initialize todo list manager and load todo list from file storage
        TodoListManager todoListManager = new TodoListManager();
        ObservableList<TodoList> todoLists = todoListManager.loadAllTodoListsFromStorage();

        // if todo lists found set todo list table values
        if (todoLists.size() > 0)
        {
            // set selected row using given row index
            selectedTodoList = todoLists.get(rowIndexToSelect);
            loadTodos();
            tblTodoLists.setItems(todoLists);
            tblTodoLists.getSelectionModel().select(rowIndexToSelect);
            tblTodoLists.getSortOrder().add(title);
        }
    }

    public void loadTodos()
    {
        // validate if a todo list is selected
        if (selectedTodoList == null) return;

        // initialize todoManager
        TodoManager todoListManager = new TodoManager();

        // get todo options
        ObservableList<Todo> todosOptions = todoListManager.getTodos(selectedTodoList.getTodos());

        // load todo table view
        tblTodos.setItems(todosOptions);
    }

    private void setupTodoListTable()
    {
        // setup the todo list table columns
        title.setCellValueFactory(new PropertyValueFactory<>("title"));

        // set click event on the todo list so we can set the selected list
        tblTodoLists.setRowFactory(todoListTableView ->
        {
            TableRow<TodoList> todoListRow = new TableRow<>();
            todoListRow.setOnMouseClicked(event ->
            {
                if (!todoListRow.isEmpty())
                {
                    selectedTodoList = todoListRow.getItem();
                    loadTodos();
                }
            });
            return todoListRow;
        });
    }

    private void setupTodoTable()
    {
        // map todo display columns
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    }

    @FXML private void addTodoList(ActionEvent event)
    {
        TodoListManager todoListManager = new TodoListManager();
        todoListManager.addTodoList(
            todoListTitle.getText(),
            tblTodoLists.getItems()
        );

        // clear title input field for next entry
        todoListTitle.clear();

        // refresh todo list;
        loadTodoLists(0);
    }

    @FXML private void addTodo(ActionEvent event)
    {
        TodoManager todoManager = new TodoManager();
        todoManager.addTodo(todoDescription, todoDueDate, selectedTodoList, tblTodos);
    }

    @FXML private void deleteSelectedTodoList(ActionEvent event)
    {
        TodoListManager todoListManager = new TodoListManager();
        todoListManager.deleteSelectedTodoList(selectedTodoList, tblTodoLists);
        loadTodoLists(0);
        loadTodos();
    }

    @FXML private void deleteSelectedTodo(ActionEvent event)
    {
        TodoManager todoManager = new TodoManager();
        todoManager.deleteSelectedTodo(selectedTodoList, tblTodos);
        loadTodos();
    }

    @FXML private void openEditListWindow(ActionEvent event)
    {
        if(selectedTodoList == null)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select a todo list to edit", "Warning");
        }
        else
        {
            // open new window for editing the selected todo list's title
            openWindow(TODO_LIST_CONTROLLER_RESOURCE_PATH, "Edit Todo List", null);
        }
    }

    @FXML private void openEditTodoWindow(ActionEvent event)
    {
        // make sure a todo is selected
        Todo selectedTodo = tblTodos.getSelectionModel().getSelectedItem();
        if(selectedTodo == null)
        {
            DialogManager dialogManager = new DialogManager();
            dialogManager.displayInfo("Please select a todo to edit", "Warning");
        }
        else
        {
            // open new window to edit selected todo
            openWindow(TODO_CONTROLLER_RESOURCE_PATH, "Edit Todo", selectedTodo);
        }
    }

    private void openWindow(String resourceFileName, String windowTitle, Todo selectedTodo)
    {
        try
        {
            // open new window using given fxml resource file path
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(resourceFileName));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(windowTitle);

            // initialize required fields for EditTodoListController
            if (resourceFileName.equals(TODO_LIST_CONTROLLER_RESOURCE_PATH))
            {
                editTodoListController = loader.getController();
                editTodoListController.setupFields(selectedTodoList);
                editTodoListController.setMainController(this);
            }
            // initialize required fields for EditTodoController
            else if (resourceFileName.equals(TODO_CONTROLLER_RESOURCE_PATH))
            {
                editTodoController = loader.getController();
                editTodoController.setupFields(selectedTodo, selectedTodoList);
                editTodoController.setMainController(this);
            }

            // display the new window
            stage.show();
        }
        catch (IOException exception)
        {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", exception);
        }
    }
}