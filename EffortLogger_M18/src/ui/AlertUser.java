package ui;

import css.Css;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

public class AlertUser {
	public static void showAlert(String title, String message, AlertType type ) {
		Css css_location = new Css();
		
		Alert alert = new Alert(type);
    	DialogPane dialogPane = alert.getDialogPane();
    	dialogPane.getStylesheets().add(
    	   css_location.getClass().getResource("application.css").toExternalForm());
    	dialogPane.getStyleClass().add("alert");
    	alert.setTitle(title);
    	alert.setHeaderText(message);
    	alert.showAndWait();
	}
}
