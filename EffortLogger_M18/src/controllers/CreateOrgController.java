package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.Org;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidInputException;
import exceptions.OrgExistsException;
import exceptions.OrgNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class CreateOrgController implements Initializable {
	@FXML
	private TextField name_tf;
	@FXML
	private PasswordField code_pf;
	@FXML
	private PasswordField confirm_code_pf;
	@FXML
	private TextArea desc_ta;
	private static Main main;
	
	public static void setMain(Main m) {
		main = m;
	}
	
	public void createOrg() {
		String name = name_tf.getText();
		String code = code_pf.getText();
		String confirm_code = confirm_code_pf.getText();
		String desc = desc_ta.getText();
		if(!code.equals(confirm_code)) {
				Alert alert = new Alert(AlertType.WARNING);
		    	DialogPane dialogPane = alert.getDialogPane();
		    	dialogPane.getStylesheets().add(
		    	   main.getClass().getResource("application.css").toExternalForm());
		    	dialogPane.getStyleClass().add("alert");
		    	alert.setTitle("Warning");
		    	alert.setHeaderText("Codes do not match");
		    	alert.showAndWait();
		}
		if(code.length() < 4) {
			Alert alert = new Alert(AlertType.WARNING);
	    	DialogPane dialogPane = alert.getDialogPane();
	    	dialogPane.getStylesheets().add(
	    	   main.getClass().getResource("application.css").toExternalForm());
	    	dialogPane.getStyleClass().add("alert");
	    	alert.setTitle("Warning");
	    	alert.setHeaderText("Please make your code at least 4 characters long");
	    	alert.showAndWait();
		}
		CompletableFuture.runAsync(() -> {
			try {
				Org o = Main.c.createOrg(name, desc, code);
				try {
					Main.c.addUserToOrg(o, Main.current_user, code);
				} catch (OrgNotFoundException e) {
					System.out.println("Error in automatically assigning current user to created org");
					e.printStackTrace();
				} catch (IncorrectPasswordException e) {
					System.out.println("Error in automatically assigning current user to created org");
					e.printStackTrace();
				}
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.INFORMATION);
			    	DialogPane dialogPane = alert.getDialogPane();
			    	dialogPane.getStylesheets().add(
			    	   main.getClass().getResource("application.css").toExternalForm());
			    	dialogPane.getStyleClass().add("alert");
			    	alert.setTitle("Success");
			    	alert.setHeaderText("Org created successfully, you were added as a member of this organization");
			    	alert.showAndWait();
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR);
			    	DialogPane dialogPane = alert.getDialogPane();
			    	dialogPane.getStylesheets().add(
			    	   main.getClass().getResource("application.css").toExternalForm());
			    	dialogPane.getStyleClass().add("alert");
			    	alert.setTitle("Error");
			    	alert.setHeaderText("Something unexpected happened");
			    	Optional<ButtonType> o = alert.showAndWait();
				});
				e.printStackTrace();
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.WARNING);
			    	DialogPane dialogPane = alert.getDialogPane();
			    	dialogPane.getStylesheets().add(
			    	   main.getClass().getResource("application.css").toExternalForm());
			    	dialogPane.getStyleClass().add("alert");
			    	alert.setTitle("Warning");
			    	alert.setHeaderText("Please fill in all fields");
			    	Optional<ButtonType> o = alert.showAndWait();
				});
				e.printStackTrace();
			} catch (OrgExistsException e) {

				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.WARNING);
			    	DialogPane dialogPane = alert.getDialogPane();
			    	dialogPane.getStylesheets().add(
			    	   main.getClass().getResource("application.css").toExternalForm());
			    	dialogPane.getStyleClass().add("alert");
			    	alert.setTitle("Warning");
			    	alert.setHeaderText("Name is already in use");
			    	alert.showAndWait();
				});
				e.printStackTrace();
			}
		});
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		desc_ta.setWrapText(true);
	}
	

	public void clear(ActionEvent e) {
		name_tf.setText("");
		code_pf.setText("");
		confirm_code_pf.setText("");
		desc_ta.setText("");
	}
}
