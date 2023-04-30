package controllers;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import application.Main;
import exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import ui.AlertUser;

public class CreateProjectController implements Initializable {
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
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	public void submit() {
		String name = name_tf.getText();
		String description = description_ta.getText();
		Date start = Date.valueOf(start_dp.getValue());
		Date end = Date.valueOf(end_dp.getValue());
		String story_points_s = story_points_tf.getText();
		int story_points = Integer.parseInt(story_points_s);
        CompletableFuture.runAsync(() -> {
        	try {
				Main.c.createProject(name, description, start, end, story_points);
				Platform.runLater(() -> {
					AlertUser.showAlert("Success", "Project created", AlertType.INFORMATION);
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.INFORMATION);
					Stage s = (Stage) name_tf.getScene().getWindow();
					s.close();
				});
				e.printStackTrace();
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Please ensure all inputs are valid", AlertType.INFORMATION);
				});
				e.printStackTrace();
			}
        });
          
	}
	public void clear() {
		name_tf.setText("");
		description_ta.setText("");
		story_points_tf.setText("");
		start_dp.setValue(null);
		end_dp.setValue(null);
	}
}
