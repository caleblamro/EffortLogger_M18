package application;
import java.util.concurrent.CompletableFuture;

import java.sql.SQLException;

import database.DatabaseConnection;
import entities.Employee;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.Page;
import javafx.scene.image.Image;


/**
 * 
 * @author Caleb Lamoreaux
 *
 */
public class Main extends Application {
	//can access DatabaseConnection from other files via Main.c
	public static DatabaseConnection c;
	//should only be set from either login or sign up page
	private static Employee current_user = null;
	private static Stage primaryStage;
	public static int WIDTH = 750;
	public static int HEIGHT = 550;

	
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) {
		Main.primaryStage = primaryStage;
		//wrap all your database calls with this, it will prevent the app from freezing up while waiting for those calls to finish
		CompletableFuture.runAsync(() -> {
			try {	
				c = new DatabaseConnection();
				System.out.println("Attempting to connect to AWS RDS");
				c.connect();
				System.out.println("Connected!");
			}catch(SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
        });
		
		Page.moveTo("Login", primaryStage, WIDTH, HEIGHT);
		primaryStage.setTitle("Effort Logger");
		primaryStage.setOnCloseRequest(e -> {
			try {
				c.disconnect();
				System.out.println("Disconnected");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		Image icon = new Image(getClass().getResourceAsStream("icon.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.show();
	}
	/**
	 * Gets the current user logged into the application
	 * @return the current user
	 */
	public static Employee getCurrentUser() {
		return current_user;
	}
	/**
	 * Sets the current user for the application
	 * @param current_user - the current user of the application
	 */
	public static void setCurrentUser(Employee current_user) {
		Main.current_user = current_user;
	}
	public static void goToSignUpPage() {
		Page.moveTo("Signup", primaryStage, WIDTH, HEIGHT);
	}
	public static void goToLoginPage() {
		Page.moveTo("Login", primaryStage, WIDTH, HEIGHT);
	}
	public static void goToDashboard() {
		if(current_user == null) {
			System.out.println("NO USER IS LOGGED IN");
			return;
		}
		if(current_user.is_manager()) {
			Page.moveTo("ManagerDashboard", primaryStage, 650, 450);
		}else {
			//navigate to the same page until employee page is made
			Page.moveTo("ManagerDashboard", primaryStage, 650, 450);
		}
	}

	public static void showOrgSelectorDialog() {
		Page.showDialog("AddOrg", 600, 350, StageStyle.UTILITY);
	}
	
	public static void showCreateOrgDialog() {
		Page.showDialog("CreateOrg", 691, 447, StageStyle.UTILITY);
	}
	public static Stage getPrimaryStage() {
		return Main.primaryStage;
	}
}
