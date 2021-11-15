/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import java.io.Serializable;

public class Todo implements Serializable
{
    private int todoId;
    private String description;
    private String dueDate;
    private String status;

    public Todo() { }

    public Todo(int todoId, String description, String dueDate)
    {
        this.description = description;
        this.dueDate = dueDate;
        this.todoId = todoId;
        this.status = "InComplete";
    }

    public int getTodoId() { return todoId; }
    public int getTodoIndex() { return todoId - 1; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
