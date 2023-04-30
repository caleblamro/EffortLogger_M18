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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator progressIndicator;
	private ArrayList<Employee> emps = null;
	CheckComboBox<Employee> employees_ccb = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadingPane.toFront();
		loadingPane.setVisible(true);
		progressIndicator.setProgress(-1.0);
		CompletableFuture.runAsync(() -> {
			try {
				emps = Main.c.getOrgEmployees(Main.getCurrentOrg());
				Platform.runLater(() -> {
					final ObservableList<Employee> employee_names = FXCollections.observableArrayList();
					for(Employee e : emps) {
						employee_names.add(e);
					}
					employees_ccb = new CheckComboBox<Employee>();
					employees_ccb.setPrefWidth(214);
					employees_ccb.getItems().addAll(employee_names);
					employees_vb.getChildren().add(employees_ccb);
					loadingPane.toBack();
					loadingPane.setVisible(false);
				    progressIndicator.setProgress(0.0);
				});
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
		});
	}
	public void createTeam() {
		String name = name_tf.getText();
		String description = description_ta.getText();
        ObservableList<Employee> es = employees_ccb.getCheckModel().getCheckedItems();
        ArrayList<Employee> selected_emps = new ArrayList<>();
        for(Employee e : es) {
        	selected_emps.add(e);
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
			} catch (UserNotFoundException e1) {
				Platform.runLater(() -> {
        			AlertUser.showAlert("Error", "One of the users selected was not found", AlertType.ERROR);
        			Stage s = (Stage) employees_vb.getScene().getWindow();
        			s.close();
        		});
				e1.printStackTrace();
			}
        });
	}
	public void clear() {
		name_tf.setText("");
		description_ta.setText("");
		employees_ccb.getCheckModel().clearChecks();
	}

}
