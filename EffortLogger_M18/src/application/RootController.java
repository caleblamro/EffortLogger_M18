package application;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import entities.Employee;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RootController {
	private String username;
	private String password;
	private static Main main = null;
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidInputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncorrectPasswordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public static void setMain(Main m) {
		main = m;
	}
	
	public void signUp(ActionEvent event) {
		main.goToSignupPage();
	}
	public void goToSigninPage(ActionEvent e) {
		main.goToSigninPage();
	}
}
