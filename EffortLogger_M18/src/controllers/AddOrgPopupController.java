package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.AutoComboBox;
import application.Main;
import entities.Org;
import exceptions.IncorrectPasswordException;
import exceptions.OrgNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class AddOrgPopupController implements Initializable {
	@FXML
	ComboBox<Org> orgs_cb;
	@FXML
	TextField code_tf;
	private ArrayList<Org> orgs;
	
	public void addOrg(ActionEvent e) {
		Org o = orgs_cb.getValue();
		try {
			System.out.println("RESULT: " + Main.c.addUserToOrg(o, Main.current_user, code_tf.getText()));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OrgNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IncorrectPasswordException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try {
			orgs = Main.c.getOrgs();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
