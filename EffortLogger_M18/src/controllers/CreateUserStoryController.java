package controllers;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.Project;
import entities.Team;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.AlertUser;
import ui.AutoComboBox;

public class CreateUserStoryController implements Initializable {
	@FXML
	TextField name_tf;
	@FXML
	TextArea description_ta;
	@FXML
	DatePicker start_dp;
	@FXML
	DatePicker end_dp;
	@FXML
	ComboBox<Team> teams_cb;
	@FXML
	ComboBox<Project> projects_cb;
	@FXML
	TextField story_points_tf;
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private ArrayList<Team> teams = null;
	private ArrayList<Project> projects = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadingPane.toFront();
		loadingPane.setVisible(true);
		progressIndicator.setProgress(-1.0);
		CompletableFuture.runAsync(() -> {
			try {
				teams = Main.c.getTeams(Main.getCurrentOrg());
				projects = Main.c.getProjects(Main.getCurrentOrg());
				Platform.runLater(() -> {
					teams_cb.setConverter(new StringConverter<Team>() {
						@Override
						public Team fromString(String s) { //THIS IS NOT HOW WE SHOULD BE FIDNING THE CORRECT OBJECT'S REFERENCE... LMK IF YOU HAVE OTHER IDEAS
							for(Team o : teams) {
								if(o == null) continue;
								String x = o.getName() + " (id: " + o.getID() + ")";
								if(x.equals(s)) {
									return o;
								}
							}
							return null;
						}

						@Override
						public String toString(Team o) {
							if(o == null) return "";
							return o.getName() + " (id: " + o.getID() +")";
						}
						
					});
					projects_cb.setConverter(new StringConverter<Project>() {
						@Override
						public Project fromString(String s) { //THIS IS NOT HOW WE SHOULD BE FIDNING THE CORRECT OBJECT'S REFERENCE... LMK IF YOU HAVE OTHER IDEAS
							for(Project o : projects) {
								if(o == null) continue;
								String x = o.getName() + " (id: " + o.getID() + ")";
								if(x.equals(s)) {
									return o;
								}
							}
							return null;
						}

						@Override
						public String toString(Project o) {
							if(o == null) return "";
							return o.getName() + " (id: " + o.getID() +")";
						}
						
					});
					for(Team t : teams) {
						teams_cb.getItems().add(t);	
					}
					for(Project p : projects) {
						projects_cb.getItems().add(p);	
					}
					AutoComboBox.autoCompleteComboBoxPlus(teams_cb, (typed_text, item) -> item.getName().toLowerCase().contains(typed_text.toLowerCase()));
					AutoComboBox.autoCompleteComboBoxPlus(projects_cb, (typed_text, item) -> item.getName().toLowerCase().contains(typed_text.toLowerCase()));
					loadingPane.toBack();
					loadingPane.setVisible(false);
				    progressIndicator.setProgress(0.0);
				});
			} catch (SQLException | InvalidInputException | UserNotFoundException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened.", AlertType.ERROR);
					e.printStackTrace();
					Stage s = (Stage) name_tf.getScene().getWindow();
					s.close();
				});
				e.printStackTrace();
			}
			
		});
	}
	public void submit() {
		String name = name_tf.getText();
		String description = description_ta.getText();
		Date start = Date.valueOf(start_dp.getValue());
		Date end = Date.valueOf(end_dp.getValue());
		Team team = teams_cb.getValue();
		Project project = projects_cb.getValue();
		String story_points_s = story_points_tf.getText();
		int story_points = Integer.parseInt(story_points_s);
		CompletableFuture.runAsync(() -> {
			try {
				Main.c.createUserStory(name, description, start, end, Main.getCurrentUser(), project, story_points, team);
				Platform.runLater(() -> {
					AlertUser.showAlert("Success", "User story was created and assigned to the product backlog", AlertType.INFORMATION);
					Stage s = (Stage) name_tf.getScene().getWindow();
					s.close();
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
					Stage s = (Stage) name_tf.getScene().getWindow();
					s.close();
				});
				e.printStackTrace();
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Please ensure all fields are valid", AlertType.ERROR);
				});
				e.printStackTrace();
			}
		});
	}
	public void clear() {
		name_tf.setText("");
		description_ta.setText("");
		projects_cb.setValue(null);
		teams_cb.setValue(null);
		start_dp.setValue(null);
		end_dp.setValue(null);
		story_points_tf.setText("");
	}
}
