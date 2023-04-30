package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import entities.Team;
import entities.UserStory;
import exceptions.InvalidInputException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.AlertUser;
import ui.AutoComboBox;

public class EmployeeDashboardController implements Initializable {
	@FXML
	Text status_t;
	@FXML
	Button start_b;
	@FXML
	Text time_t;
	@FXML
	ComboBox<UserStory> stories_cb;
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private ArrayList<UserStory> stories = null;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			stories = Main.c.getTeamUserStories();
		} catch (SQLException | InvalidInputException e) {
			AlertUser.showAlert("Error", "Could not get user stories for selected team", AlertType.ERROR);
			return;
		}
		stories_cb.setConverter(new StringConverter<UserStory>() {
			@Override
			public UserStory fromString(String s) { //THIS IS NOT HOW WE SHOULD BE FIDNING THE CORRECT OBJECT'S REFERENCE... LMK IF YOU HAVE OTHER IDEAS
				for(UserStory o : stories) {
					if(o == null) continue;
					String x = o.getName() + " (id: " + o.getID() + ")";
					if(x.equals(s)) {
						return o;
					}
				}
				return null;
			}

			@Override
			public String toString(UserStory o) {
				if(o == null) return "";
				return o.getName() + " (id: " + o.getID() +")";
			}
			
		});
		stories_cb.getItems().addAll(stories);
		AutoComboBox.autoCompleteComboBoxPlus(stories_cb, (typed_text, item) -> item.getName().toLowerCase().contains(typed_text.toLowerCase()));
	}
	public void addDefect() {
		
	}
	public void start() {
		
	}
}
