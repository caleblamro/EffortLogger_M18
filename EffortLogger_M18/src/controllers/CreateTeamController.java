package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import org.controlsfx.control.CheckComboBox;

import application.Main;
import entities.Employee;
import exceptions.InvalidInputException;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.AlertUser;

public class CreateTeamController implements Initializable {
	@FXML
	TextField name_tf;
	@FXML
	TextArea description_ta;
	@FXML
	VBox employees_vb;
	
	private ArrayList<Employee> emps = null;
	CheckComboBox<String> employees_ccb = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try {
			emps = Main.c.getOrgEmployees(Main.getCurrentOrg());
		} catch (SQLException e) {					
			AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) employees_vb.getScene().getWindow();
			s.close();
			return;
		} catch (InvalidInputException e) {					
			AlertUser.showAlert("Error", "Could not get employees without a valid organization being selected", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) employees_vb.getScene().getWindow();
			s.close();
			return;
		} catch (UserNotFoundException e) {					
			AlertUser.showAlert("Error", "Organization was not found", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) employees_vb.getScene().getWindow();
			s.close();
			return;
		}
		final ObservableList<String> employee_names = FXCollections.observableArrayList();
		for(Employee e : emps) {
			employee_names.add(e.getName());
		}
		employees_ccb = new CheckComboBox<String>();
		employees_ccb.setPrefWidth(214);
		employees_ccb.getItems().addAll(employee_names);
		employees_vb.getChildren().add(employees_ccb);
	}
	public void createTeam() {
		String name = name_tf.getText();
		String description = description_ta.getText();
        ObservableList<String> es = employees_ccb.getCheckModel().getCheckedItems();
        ArrayList<Employee> selected_emps = new ArrayList<>();
        for(String s : es) {
        	for(Employee e : emps) {
        		if(e.getName().equals(s)) {
        			selected_emps.add(e);
        		}
        	}
        }
        CompletableFuture.runAsync(() -> {
        	Employee m;
        	if(Main.getCurrentUser().is_manager()){
        		m = Main.getCurrentUser();
        	}else {
        		Platform.runLater(() -> {
        			AlertUser.showAlert("Error", "You must be a manager to create a team", AlertType.ERROR);
        			Stage s = (Stage) employees_vb.getScene().getWindow();
        			s.close();
        		});
        		return;
        	}
        	try {
				Main.c.createTeam(name, description, m, Main.getCurrentOrg(), selected_emps);
				Platform.runLater(() -> {
        			AlertUser.showAlert("Success", "Team was created", AlertType.INFORMATION);
        			Stage s = (Stage) employees_vb.getScene().getWindow();
        			s.close();
        		});
				return;
			} catch (SQLException e) {
				Platform.runLater(() -> {
        			AlertUser.showAlert("Error", "Something unexpected happened", AlertType.ERROR);
        			Stage s = (Stage) employees_vb.getScene().getWindow();
        			s.close();
        		});
				e.printStackTrace();
				return;
			} catch (InvalidInputException e) {
				Platform.runLater(() -> {
        			AlertUser.showAlert("Error", "Please ensure all inputs are valid", AlertType.ERROR);
        			Stage s = (Stage) employees_vb.getScene().getWindow();
        			s.close();
        		});
				e.printStackTrace();
				return;
			}
        });
	}
	public void clear() {
		name_tf.setText("");
		description_ta.setText("");
		employees_ccb.getCheckModel().clearChecks();
	}

}
