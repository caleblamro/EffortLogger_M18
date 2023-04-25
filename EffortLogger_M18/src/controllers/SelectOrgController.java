package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import entities.Org;
import exceptions.InvalidInputException;
import exceptions.OrgNotFoundException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.AlertUser;
import ui.AutoComboBox;

public class SelectOrgController implements Initializable{
	@FXML
	ComboBox<Org> orgs_cb;
	@FXML
	Button submit_b;
	
	private ArrayList<Org> orgs = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try {
			orgs = Main.c.getOrgs(Main.getCurrentUser());
		} catch (SQLException e) {
			AlertUser.showAlert("Error", "Something unexpected happened.", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) submit_b.getScene().getWindow();
			s.close();
		} catch (InvalidInputException e) {
			AlertUser.showAlert("Error", "The current user was not valid.", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) submit_b.getScene().getWindow();
			s.close();
		} catch (OrgNotFoundException e) {
			AlertUser.showAlert("Error", "An organization with an invalid ID was one of your organizations.", AlertType.ERROR);
			e.printStackTrace();
			Stage s = (Stage) submit_b.getScene().getWindow();
			s.close();
		}
		orgs_cb.setConverter(new StringConverter<Org>() {

			@Override
			public Org fromString(String s) { //THIS IS NOT HOW WE SHOULD BE FIDNING THE CORRECT OBJECT'S REFERENCE... LMK IF YOU HAVE OTHER IDEAS
				for(Org o : orgs) {
					if(o == null) continue;
					if(o.getName().equals(s)) {
						return o;
					}
				}
				return null;
			}

			@Override
			public String toString(Org o) {
				if(o == null) return "";
				return o.getName();
			}
			
		});
		for(Org o : orgs) {
			orgs_cb.getItems().add(o);	
		}
		AutoComboBox.autoCompleteComboBoxPlus(orgs_cb, (typed_text, item) -> item.getName().toLowerCase().contains(typed_text.toLowerCase()));
	}
	public void submit() {
		Org o = orgs_cb.getValue();
		Main.setCurrentOrg(o);
		Stage s = (Stage) submit_b.getScene().getWindow();
		s.close();
	}
}
