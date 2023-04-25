package controllers;

import application.Main;
import javafx.event.ActionEvent;

public class ManagerDashboardController {
	
	public void showCreateTeam() {
		Main.showCreateTeamDialog();
	}
	public void showCreateProject(){
		Main.showCreateProjectDialog();
	}
	public void showCreateUserStory() {
		Main.showCreateUserStoryDialog();
	}
}
