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
import ui.AlertUser;

public class CreateOrgController implements Initializable {
	@FXML
	private TextField name_tf;
	@FXML
	private PasswordField code_pf;
	@FXML
	private PasswordField confirm_code_pf;
	@FXML
	private TextArea desc_ta;
	@FXML
	private TextField units_tf;
	public void createOrg() {
		String name = name_tf.getText();
		String code = code_pf.getText();
		String confirm_code = confirm_code_pf.getText();
		String desc = desc_ta.getText();
		String sunits = units_tf.getText();
		int units;
		try {
			units = Integer.parseInt(sunits);
			if(units <= 0) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			AlertUser.showAlert("Warning", "You must enter a number of days greater than zero", AlertType.WARNING);
			return;
		}
		if(!code.equals(confirm_code)) {
			AlertUser.showAlert("Error", "Provided codes do not match", AlertType.ERROR);
			return;
		}
		if(code.length() <= 4) {
			AlertUser.showAlert("Warning", "Please make your code at least four characters long", AlertType.WARNING);
			return;
		}
		CompletableFuture.runAsync(() -> {
			try {
				Org o = Main.c.createOrg(name, desc, code, units);
				try {
					Main.c.addUserToOrg(o, Main.current_user, code);
				} catch (OrgNotFoundException e) {
					System.out.println("Error in automatically assigning current user to created org");
					e.printStackTrace();
					return;
				} catch (IncorrectPasswordException e) {
					System.out.println("Error in automatically assigning current user to created org");
					e.printStackTrace();
					return;
				}
				Platform.runLater(() -> {
					AlertUser.showAlert("Success", "The organization was created! You were added as a member", AlertType.INFORMATION);
					return;
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
				});
				e.printStackTrace();
				return;
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Warning", "Please fill in all fields correctly", AlertType.WARNING);
				});
				e.printStackTrace();
				return;
			} catch (OrgExistsException e) {

				Platform.runLater(() -> {
					AlertUser.showAlert("Warning", "Please use another name, that one already exists", AlertType.WARNING);
				});
				e.printStackTrace();
				return;
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
