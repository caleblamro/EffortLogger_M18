package ui;

import java.io.IOException;

import css.Css;
import fxml.Fxml;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Page {
	static Css css_location = new Css();
	static Fxml fxml_location = new Fxml();
	public static void move(String fxml_file_name, Stage primaryStage, int WIDTH, int HEIGHT) {
		Parent signup = null;
		try {
			signup = FXMLLoader.load(fxml_location.getClass().getResource(fxml_file_name+".fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(signup, WIDTH, HEIGHT);
		scene.getStylesheets().add(css_location.getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
	}
	public static void showDialog(String fxml_file_name, int WIDTH, int HEIGHT, StageStyle style) {
		Stage c = new Stage();
		c.initStyle(style);
		Parent dialog = null;
		try {
			dialog = FXMLLoader.load(fxml_location.getClass().getResource(fxml_file_name+".fxml"));
		} catch (IOException el) {
			el.printStackTrace();
		}
		Scene s = new Scene(dialog, WIDTH, HEIGHT);
		s.getStylesheets().add(css_location.getClass().getResource("application.css").toExternalForm());
		c.setScene(s);
		c.show();
	}
}
