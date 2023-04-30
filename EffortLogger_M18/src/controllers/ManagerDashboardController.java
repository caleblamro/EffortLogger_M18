package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.Defect;
import entities.Project;
import entities.Team;
import entities.UserStory;
import exceptions.InvalidInputException;
import exceptions.NotManagerException;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.AlertUser;
import ui.DefectList;
import ui.Page;
import ui.ProjectList;
import ui.TeamList;
import ui.UserStoryList;

public class ManagerDashboardController implements Initializable {
	@FXML
	private Text delivery_t;
	private ArrayList<Project> projects = null;
	private ArrayList<Team> teams = null;
	private ArrayList<UserStory> user_stories = null;
	private ArrayList<Defect> defects = null;
	@FXML
	private Accordion teams_a;
	private TeamList team_list;
	private ProjectList project_list;
	private UserStoryList user_story_list;
	private DefectList defect_list;
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	Text story_points_t;
	@FXML
	Text defects_t;
	@FXML
	Text completed_t;
	
	public void showCreateTeam() {
		Main.showCreateTeamDialog();
	}
	public void showCreateProject(){
		Main.showCreateProjectDialog();
	}
	public void showCreateUserStory() {
		Main.showCreateUserStoryDialog();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadingPane.toFront();
		loadingPane.setVisible(true);
		progressIndicator.setProgress(-1.0);
		CompletableFuture.runAsync(() -> {
			try {
				teams = Main.c.getTeams(Main.getCurrentUser());
				projects = Main.c.getProjects(Main.getCurrentOrg());
				user_stories = Main.c.getUserStories(Main.getCurrentUser());
				defects = Main.c.getActiveDefects();
			} catch (SQLException | InvalidInputException | UserNotFoundException | NotManagerException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened.", AlertType.ERROR);
					e.printStackTrace();
					Stage s = (Stage) teams_a.getScene().getWindow();
					s.close();
				});
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				team_list = new TeamList("Teams", teams);
				project_list = new ProjectList("Projects", projects);
				user_story_list = new UserStoryList("User Stories", user_stories);
				defect_list = new DefectList("Defects", defects);
				teams_a.getPanes().addAll(user_story_list, team_list, project_list, defect_list);
				defects_t.setText(defects.size() + "");
				int story_points = 0;
				int completed = 0;
				for(UserStory u : user_stories) {
					if(u == null) continue;
					if(u.getActualEndDate() == null) {
						story_points += u.getStoryPoints();
					}else {
						completed += u.getStoryPoints();
					}
				}
				completed_t.setText("" + completed);
				story_points_t.setText(""+story_points);
				loadingPane.toBack();
				loadingPane.setVisible(false); 
			    progressIndicator.setProgress(0.0);
			});
		});
	}
	public void refresh() {
		teams_a.getScene().setCursor(Cursor.WAIT);
		CompletableFuture.runAsync(() -> {
			try {
				teams = Main.c.getTeams(Main.getCurrentUser());
				projects = Main.c.getProjects(Main.getCurrentOrg());
				user_stories = Main.c.getUserStories(Main.getCurrentUser());
				Platform.runLater(() -> {
					team_list.update(teams);
					project_list.update(projects);
					teams_a.getScene().setCursor(Cursor.DEFAULT);
				});
			} catch (SQLException e) {
				Platform.runLater(() -> {
					AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
					teams_a.getScene().setCursor(Cursor.DEFAULT);
				});
				e.printStackTrace();
			} catch (InvalidInputException | UserNotFoundException | NotManagerException e) {
		        Platform.runLater(() -> {
		            AlertUser.showAlert("Error", "Either the user or organization was deleted from the database after you logged in. Please restart the program", AlertType.ERROR);
		            teams_a.getScene().setCursor(Cursor.DEFAULT);
		        });
		        e.printStackTrace();
		    }
		});
	}
	public void showCompleteUserStory() {
		Main.showCompleteUserStoriesDialog();
	}
}
