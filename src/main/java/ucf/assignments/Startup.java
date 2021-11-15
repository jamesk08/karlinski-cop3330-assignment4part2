/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 James Karlinski
 */

package ucf.assignments;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Startup extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        Parent root = null;
        URL mainFxmlPath = getClass().getResource("Main.fxml");
        try
        {
            root = FXMLLoader.load(mainFxmlPath);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Todo App");
            primaryStage.show();
        }
        catch (IOException e)
        {
            System.out.println("Failed to load main.fxml file.");
            e.printStackTrace();
        }
    }
}
