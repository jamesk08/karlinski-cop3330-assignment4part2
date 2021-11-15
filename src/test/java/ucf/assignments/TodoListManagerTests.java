/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TodoListManagerTests
{
    public static String todoListStoragePath = "src\\test\\java\\TodoLists";

    @Test
    public void findNextTodoListId_ShouldReturnExpected()
    {
        // assert
        final int EXPECTED_TODO_LIST_ID = 3;
        ArrayList<TodoList> todoListArray = new ArrayList<>();
        todoListArray.add(new TodoList(1, "First"));
        todoListArray.add(new TodoList(2, "Second"));
        ObservableList observableTodoLists = FXCollections.observableArrayList(todoListArray);

        // act
        TodoListManager todoListManager = new TodoListManager(todoListStoragePath);
        int nextTodoListId = todoListManager.findNextTodoListId(observableTodoLists);

        // assert
        assertEquals(nextTodoListId, EXPECTED_TODO_LIST_ID);
    }

    @Test
    public void addTodoList_ShouldCreateNewTodoListFile()
    {
        // assert
        final String EXPECTED_TITLE = "addTodoListTest";
        TodoList todoList = new TodoList(1501, "Test1");
        ObservableList observableTodoLists = FXCollections.observableArrayList(todoList);

        // act addTodoList
        TodoListManager todoListManager = new TodoListManager(todoListStoragePath);
        todoListManager.addTodoList(EXPECTED_TITLE, observableTodoLists);

        // assert
        ObservableList<TodoList> todoLists = todoListManager.loadAllTodoListsFromStorage();

        boolean contains = false;
        for (TodoList todoListItem: todoLists)
        {
            if(Objects.equals(todoListItem.getTitle(), EXPECTED_TITLE))
            {
                contains = true;
                break;
            }
        }

        // assert
        assertTrue(contains);

        cleanupFiles();
    }

    @Test
    public void getTodoListFileByName_shouldReturnExpected()
    {
        // assert
        final String EXPECTED_FILE_NAME = "Groceries";
        final String EXPECTED_PATH = todoListStoragePath + "\\" + EXPECTED_FILE_NAME;
        TodoListManager todoListManager = new TodoListManager(todoListStoragePath);

        // act
        File file = todoListManager.getTodoListFileByName(EXPECTED_FILE_NAME);
        assertNotNull(file);
        assertEquals(file.getPath(), EXPECTED_PATH);
    }

    @Test
    public void getTodoListFileById_shouldReturnExpected()
    {
        // assert
        final int EXPECTED_FILE_ID = 500;
        final String EXPECTED_PATH = todoListStoragePath + "\\" + EXPECTED_FILE_ID + ".txt";
        TodoListManager todoListManager = new TodoListManager(todoListStoragePath);

        // act
        File file = todoListManager.getTodoListFileById(EXPECTED_FILE_ID);
        assertNotNull(file);
        assertEquals(file.getPath(), EXPECTED_PATH);
    }

    private void cleanupFiles()
    {
        File dir = new File(todoListStoragePath);
        if (!dir.isDirectory()) return;

        for (File file : Objects.requireNonNull(dir.listFiles()))
        {
            String filePath = MessageFormat.format("{0}\\{1}", todoListStoragePath, file.getName());
            File fileToDelete = new File(filePath);
            fileToDelete.delete();
        }
    }
}
