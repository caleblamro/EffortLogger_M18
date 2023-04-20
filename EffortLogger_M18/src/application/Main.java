package application;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import controllers.LoginController;
import controllers.SignUpController;

import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnection;
import entities.Employee;
import entities.Org;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.OrgExistsException;
import exceptions.UserNotFoundException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;


/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class Main extends Application {
	public static DatabaseConnection c;
	public static Employee current_user = null;
	public Stage primaryStage;
	private static int WIDTH = 750;
	private static int HEIGHT = 550;
	
	@Override
	public void start(Stage primaryStage) {
		LoginController.setMain(this);
		SignUpController.setMain(this);
		this.primaryStage = primaryStage;
		//wrap all your database calls with this, it will prevent the app from freezing up while waiting for those calls to finish
		//SHOW LOGIN/SIGNUP HERE	
		CompletableFuture.runAsync(() -> {
			try {	
				c = new DatabaseConnection();
				System.out.println("Attempting to connect to AWS RDS...");
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
			root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Scene scene = new Scene(root, WIDTH, HEIGHT);
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
		Image icon = new Image(getClass().getResourceAsStream("icon.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	public void goToSignupPage() {
		Parent signup = null;
		try {
			signup = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(signup, WIDTH, HEIGHT);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
	}
	public void goToSigninPage() {
		Parent signup = null;
		try {
			signup = FXMLLoader.load(getClass().getResource("Login.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(signup, WIDTH, HEIGHT);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
	}
	public static Employee getCurrentUser() {
		return current_user;
	}
	public void setCurrentUser(Employee current_user) {
		Main.current_user = current_user;
	}
	
	public void showOrgSelectorDialog() {
		Stage c = new Stage();
		c.initStyle(StageStyle.UTILITY);
		Parent add_org_popup = null;
		try {
			add_org_popup = FXMLLoader.load(getClass().getResource("AddOrg.fxml"));
		} catch (IOException el) {
			el.printStackTrace();
		}
		Scene s = new Scene(add_org_popup, 600, 350);
		s.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		c.setScene(s);
		c.show();
	}
}
