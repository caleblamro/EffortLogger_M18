package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import entities.Team;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.AlertUser;
import ui.AutoComboBox;

public class SelectTeamController implements Initializable {
	@FXML
	private ComboBox<Team> teams_cb;
	private ArrayList<Team> teams;
 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			teams = Main.c.getEmployeeTeams();
		} catch (SQLException | InvalidInputException | UserNotFoundException e) { 
			AlertUser.showAlert("Error", "Something unexpected happened. Try restarting the program", AlertType.ERROR);
			e.printStackTrace();
			return;
		}
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
		teams_cb.getItems().addAll(teams);
		AutoComboBox.autoCompleteComboBoxPlus(teams_cb, (typed_text, item) -> item.getName().toLowerCase().contains(typed_text.toLowerCase()));
	}
	public void submit() {
		Team t = teams_cb.getValue();
		if(t == null || t.getID() == -1) {
			AlertUser.showAlert("Error", "You must select a valid team. If you keep seeing this error try restarting the program.", AlertType.ERROR);
			return;
		}
		AlertUser.showAlert("Success", "Team selected", AlertType.INFORMATION);
		Main.setCurrentTeam(t);
		Stage s = (Stage) teams_cb.getScene().getWindow();
		s.close();
		
	}
}
