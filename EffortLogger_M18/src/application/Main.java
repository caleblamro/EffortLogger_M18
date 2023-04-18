package application;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnection;
import entities.Employee;
import entities.Org;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Main extends Application {
	DatabaseConnection c;
	@Override
	public void start(Stage primaryStage) {
			try {
				c = new DatabaseConnection();
				System.out.println("Attempting to connect to local server...");
				c.connect();
				System.out.println("Connection created!");
				Thread.sleep(2000);
				System.out.println("Attempting to log into test_user_1");
				Employee e = c.signIn("test_user_1", "pass");
				System.out.println("LOGGED IN AS " + e.getName());
				
			} catch (InvalidInputException e) {
				e.printStackTrace();
				return;
			} catch (UserNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IncorrectPasswordException e1) {
				e1.printStackTrace();
				return;
			}catch(Exception e) {
				System.out.println("Could not connect... \nPrinting error:");
				System.out.println(e);
			}
			
			
			primaryStage.setTitle("Effort Logger");
			Parent root = null;
			try {
				root = FXMLLoader.load(getClass().getResource("ManagerDashboard.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Scene scene = new Scene(root,660,460);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setOnCloseRequest(e -> {
				try {
					c.disconnect();
					System.out.println("DISCONNECTED");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
			primaryStage.setScene(scene);
			primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
