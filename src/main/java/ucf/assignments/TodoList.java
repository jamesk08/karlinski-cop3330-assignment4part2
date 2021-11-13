/*
 *  UCF COP3330 Fall 2021 Assignment 5 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import java.io.Serializable;
import java.util.ArrayList;

public class TodoList implements Serializable
{
    private String title;
    private ArrayList<Todo> todos = new ArrayList<>();
    private int todoListId;

    public TodoList(int todoListId, String title)
    {
        this.title = title;
        this.todoListId = todoListId;
    }

    public String getTitle() {return this.title; };
    public void setTitle(String value) { this.title = value; };

    public ArrayList<Todo> getTodos() { return this.todos; };
    public void addTodos(Todo todo) { this.todos.add(todo); };

    public int getTodoListId() { return this.todoListId; };
    public int getTodoListIndex() { return this.todoListId - 1; };
}

