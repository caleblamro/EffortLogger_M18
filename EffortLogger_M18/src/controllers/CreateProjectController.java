package controllers;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import application.Main;
import entities.UserStory;
import exceptions.InvalidInputException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.AlertUser;

public class CreateProjectController implements Initializable {
	@FXML
	VBox stories_vb;
	@FXML
	TextField name_tf;
	@FXML
	TextField story_points_tf;
	@FXML
	TextArea description_ta;
	@FXML
	DatePicker start_dp;
	@FXML
	DatePicker end_dp;
	CheckComboBox<String> user_stories_ccb = null;
	private ArrayList<UserStory> stories = null;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			stories = Main.c.getUserStories(Main.getCurrentOrg());
		} catch (SQLException e) {
			AlertUser.showAlert("Error", "Something unexpected happened.", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) stories_vb.getScene().getWindow();
			s.close();
			return;
		} catch (InvalidInputException e) {
			AlertUser.showAlert("Error", "Selected organization was null or does not exist. Try to restart the program.", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) stories_vb.getScene().getWindow();
			s.close();
			return;
		}
		final ObservableList<String> user_stories = FXCollections.observableArrayList();
		for(UserStory u : stories) {
			user_stories.add(u.getName());
		}
		user_stories_ccb = new CheckComboBox<String>();
		user_stories_ccb.setPrefWidth(214);
		user_stories_ccb.getItems().addAll(user_stories);
		stories_vb.getChildren().add(user_stories_ccb);
	}
	public void submit() {
		
	}
	public void clear() {
		
	}
}
