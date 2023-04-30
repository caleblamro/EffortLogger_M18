package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import org.controlsfx.control.CheckComboBox;

import application.Main;
import entities.Employee;
import entities.UserStory;
import exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.AlertUser;

public class CompleteUserStoryController implements Initializable {
	@FXML
	VBox user_stories_vb;
	ArrayList<UserStory> user_stories;
	CheckComboBox<UserStory> stories_ccb = null;
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator progressIndicator;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadingPane.toFront();
		loadingPane.setVisible(true);
		progressIndicator.setProgress(-1.0);
		CompletableFuture.runAsync(() -> {
			try {
				user_stories = Main.c.getActiveUserStories(Main.getCurrentUser());
				Platform.runLater(() -> {
					final ObservableList<UserStory> employee_names = FXCollections.observableArrayList();
					for(UserStory e : user_stories) {
						employee_names.add(e);
					}
					stories_ccb = new CheckComboBox<UserStory>();
					stories_ccb.setPrefWidth(214);
					stories_ccb.getItems().addAll(employee_names);
					user_stories_vb.getChildren().add(stories_ccb);
					loadingPane.toBack();
					loadingPane.setVisible(false);
				    progressIndicator.setProgress(0.0);
				});
			}catch (SQLException | InvalidInputException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
					Stage s = (Stage) user_stories_vb.getScene().getWindow();
					s.close();
				});
				e.printStackTrace();
			}
		});
	}
	public void submit() {
        ObservableList<UserStory> es = stories_ccb.getCheckModel().getCheckedItems();
        ArrayList<UserStory> selected_emps = new ArrayList<>();
        for(UserStory e : es) {
        	selected_emps.add(e);
        }
        user_stories_vb.getScene().setCursor(Cursor.WAIT);
        CompletableFuture.runAsync(() -> {
        	try {
				Main.c.completeUserStories(selected_emps);
				Platform.runLater(() -> {
					AlertUser.showAlert("Success", "User stories completed", AlertType.INFORMATION);
					Stage s = (Stage) user_stories_vb.getScene().getWindow();
					s.close();
				});
			} catch (SQLException e1) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Could not complete user stories", AlertType.ERROR);
					Stage s = (Stage) user_stories_vb.getScene().getWindow();
					s.close();
				});
			}
        });
	}
}
