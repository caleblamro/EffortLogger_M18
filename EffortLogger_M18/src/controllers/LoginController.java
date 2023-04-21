package controllers;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.Employee;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ui.AlertUser;
import ui.Page;
import javafx.scene.control.Alert.AlertType;

public class LoginController {
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
	
	public void login(ActionEvent event) {
		System.out.println("ATTEMPTING TO LOGIN");
		CompletableFuture.runAsync(() -> {
			try {
				Employee e = Main.c.signIn(username_tf.getText(), password_pf.getText());
				System.out.println("EMPLOYEE:\n" + e);
				Main.setCurrentUser(e);
				Platform.runLater(() -> {
					AlertUser.showAlert("Success", "Logged in successfully", AlertType.INFORMATION);
		    		Main.goToDashboard();
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {					
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
				});
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
		Main.goToSignUpPage();
	}
}
