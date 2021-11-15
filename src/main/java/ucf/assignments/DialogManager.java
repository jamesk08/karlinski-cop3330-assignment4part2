/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import javax.swing.JOptionPane;

public class DialogManager
{
    public void displayInfo(String message, String title)
    {
        // display info and warning messages for form fields that has invalid value
        JOptionPane.showMessageDialog(
            null,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    public void displayError(String message, String title)
    {
        // display error message dialogs
        JOptionPane.showMessageDialog(
            null,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
}
