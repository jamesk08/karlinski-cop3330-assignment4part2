package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoListManagerTests
{
    public static String TODO_LIST_STORAGE_PATH = "src/test/java/TodoLists";

    @Test
    public void findNextTodoListId_Should_Return_Expected()
    {
        // assert
        final int EXPECTED_TODO_LIST_ID = 3;
        ArrayList<TodoList> todoListsAl = new ArrayList<>();
        todoListsAl.add(new TodoList(1, "First"));
        todoListsAl.add(new TodoList(2, "Second"));
        ObservableList todoLists = FXCollections.observableArrayList(todoListsAl);

        // act
        TodoListManager todoListManager = new TodoListManager();
        int nextTodoListId = todoListManager.findNextTodoListId(todoLists);

        // assert
        assertEquals(nextTodoListId, EXPECTED_TODO_LIST_ID);
    }
}
