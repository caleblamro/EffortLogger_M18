package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import application.AutoComboBox;
import application.Main;
import entities.Employee;
import entities.Org;
import exceptions.InvalidInputException;
import exceptions.PasswordsDoNotMatchException;
import exceptions.UsernameTakenException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class SignUpController {
	//ALL CONTROLLERS NEED THIS STATIC REFERENCE TO MAIN, OR WE WON'T BE ABLE TO NAVIGATE THROUGH PAGES
	private static Main main = null;
	@FXML
	private TextField full_name_tf;
	@FXML
	private TextField username_tf;
	@FXML
	private PasswordField password_pf;
	@FXML
	private PasswordField confirm_password_pf;
	@FXML
	private CheckBox is_manager_cb;
	
	public static void setMain(Main m) {
		main = m;
	}
	
	public void signUp(ActionEvent e) throws PasswordsDoNotMatchException {
		if(!password_pf.getText().equals(confirm_password_pf.getText())) {
			throw new PasswordsDoNotMatchException();
		}
		CompletableFuture.runAsync(() -> {
			try {
				Employee emp = Main.c.signUp(full_name_tf.getText(), username_tf.getText(), password_pf.getText(), is_manager_cb.isSelected(), new ArrayList<>());
				main.setCurrentUser(emp);
				//VERY IMPORTANT THAT YOU RUN FX METHODS ON THE FX THREAD... DO NOT TRY TO OPEN A NEW WINDOW WITHOUT USING Platform.runLater()
				Platform.runLater(() -> {
					main.showOrgSelectorDialog();
				});
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UsernameTakenException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidInputException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	public void goToSigninPage(ActionEvent e) {
		main.goToSigninPage();
	}
	public void clear(ActionEvent e) {
		full_name_tf.setText("");
		username_tf.setText("");
		password_pf.setText("");
		confirm_password_pf.setText("");
		is_manager_cb.setSelected(false);
	}
	
}
