package application;
import java.util.concurrent.CompletableFuture;

import java.sql.SQLException;

import database.DatabaseConnection;
import entities.Employee;
import entities.Org;
import entities.Team;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.AlertUser;
import ui.Page;
import javafx.scene.control.Alert.AlertType;
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
	private static Org current_org = null;
	private static Team current_team = null;
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
				c.connect();
			}catch(SQLException e) {
				AlertUser.showAlert("Error", "Could not connect to the database. Check your internet connection", AlertType.ERROR);
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e1) {
				AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
				e1.printStackTrace();
				return;
			}
        });
		
		Page.moveTo("Login", primaryStage, WIDTH, HEIGHT);
		primaryStage.setTitle("Effort Logger");
		primaryStage.setOnCloseRequest(e -> {
			try {
				c.disconnect();
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
			AlertUser.showAlert("Error", "No user was detected, try restarting the program", AlertType.ERROR);
			return;
		}
		Page.showDialog("SelectOrg", 299, 190, StageStyle.UTILITY);
		while(current_org == null) {
			AlertUser.showAlert("Error", "You must select an organization", AlertType.ERROR);
			Page.showDialog("SelectOrg", 299, 190, StageStyle.UTILITY);
		}
		if(current_user.is_manager()) {
			Page.moveTo("ManagerDashboard", primaryStage, 650, 447);
		}else {
			//navigate to the same page until employee page is made
			Page.showDialog("SelectTeam", 299, 190, StageStyle.UTILITY);
			while(current_team == null) {
				AlertUser.showAlert("Error", "You must select a team", AlertType.ERROR);
				Page.showDialog("SelectTeam", 299, 190, StageStyle.UTILITY);
			}
			Page.moveTo("EmployeeDashboard", primaryStage, 640, 179);
		}
	}

	public static void showOrgSelectorDialog() {
		Page.showDialog("AddOrg", 600, 350, StageStyle.UTILITY);
	}
	
	public static void showCreateOrgDialog() {
		Page.showDialog("CreateOrg", 691, 447, StageStyle.UTILITY);
	}
	public static void showCreateTeamDialog() {
		Page.showDialog("CreateTeam", 601, 401, StageStyle.UTILITY);
	}
	public static void showCreateProjectDialog() {
		Page.showDialog("CreateProject", 602, 442, StageStyle.UTILITY);
	}
	public static void showCreateUserStoryDialog() {
		Page.showDialog("CreateUserStory", 600, 500, StageStyle.UTILITY);
	}
	public static void showCompleteUserStoriesDialog() {
		Page.showDialog("CompleteUserStory", 524, 242, StageStyle.UTILITY);
	}
	public static Stage getPrimaryStage() {
		return Main.primaryStage;
	}
	public static Org getCurrentOrg() {
		return current_org;
	}
	public static void setCurrentTeam(Team t) {
		current_team = t;
	}
	public static Team getCurrentTeam() {
		return current_team;
	}
	public static void setCurrentOrg(Org o) {
		current_org = o;
	}
}
