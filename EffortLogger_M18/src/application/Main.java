package application;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnection;
import entities.Employee;
import entities.Org;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class Main extends Application {
	public static DatabaseConnection c;
	public static Employee current_user = null;
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		//wrap all your database calls with this, it will prevent the app from freezing up while waiting for those calls to finish
		//SHOW LOGIN/SIGNUP HERE	
		CompletableFuture.runAsync(() -> {
			try {	
				c = new DatabaseConnection();
				System.out.println("Attempting to connect to local server...");
				c.connect();
				System.out.println("Connected!");
			}catch(SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
        });
		
			
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("Root.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Scene scene = new Scene(root, 750,550);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Effort Logger");
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
	public static void goToSignupPage() {
		System.out.println("Go to signup page");
	}

	public static Employee getCurrentUser() {
		return current_user;
	}
	public static void setCurrentUser(Employee current_user) {
		Main.current_user = current_user;
		System.out.println("current_user is set in Main");
		//navigate to new page here
	}
}
