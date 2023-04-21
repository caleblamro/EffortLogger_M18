package controllers;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.Employee;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ui.AlertUser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class LoginController {
	//ALL CONTROLLERS NEED THIS STATIC REFERENCE TO MAIN, OR WE WON'T BE ABLE TO NAVIGATE THROUGH PAGES
	private static Main main = null;
	private String username;
	private String password;
	@FXML
	private TextField username_tf;
	@FXML
	private PasswordField password_pf;
	
	@FXML
	void setUsername(ActionEvent event) {
		Object source = event.getSource();
	    if (source instanceof TextField) {
	        TextField textField = (TextField) source;
	        username = textField.getText();
			System.out.println("USERNAME: " + username);
	    }
	}
	@FXML
	void setPassword(ActionEvent event) {
		Object source = event.getSource();
	    if (source instanceof PasswordField) {
	        PasswordField passwordField = (PasswordField) source;
	        password = passwordField.getText();
			System.out.println("PASSWORD: " + password);
	    }
	}
	public static void setMain(Main m) {
		main = m;
	}
	
	public void login(ActionEvent event) {
		System.out.println("ATTEMPTING TO LOGIN");
		CompletableFuture.runAsync(() -> {
			try {
				Employee e = Main.c.signIn(username_tf.getText(), password_pf.getText());
				System.out.println("EMPLOYEE:\n" + e);
				main.setCurrentUser(e);
				Platform.runLater(() -> {
//			    	Alert alert = new Alert(AlertType.INFORMATION);
//			    	DialogPane dialogPane = alert.getDialogPane();
//			    	dialogPane.getStylesheets().add(
//			    	   main.getClass().getResource("application.css").toExternalForm());
//			    	dialogPane.getStyleClass().add("alert");
//			    	alert.setTitle("Success");
//			    	alert.setHeaderText("Logged in successfully");
//			    	Optional<ButtonType> o = alert.showAndWait();
//			    	if(o.isPresent()) {
//			    		main.goToDashboard();
//			    	}
		    		main.goToDashboard();
				});
			} catch (SQLException e) {
				AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
				e.printStackTrace();
			} catch (UserNotFoundException e) {
			Platform.runLater(() -> {
				AlertUser.showAlert("Error", "User does not exist", AlertType.ERROR);
			});
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Warning", "Please fill in all fields", AlertType.WARNING);
				});
				e.printStackTrace();
			} catch (IncorrectPasswordException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Incorrect password", AlertType.WARNING);
				});
			}
		});
	}
	
	public void goToSignUpPage(ActionEvent event) {
		main.goToSignupPage();
	}
}
