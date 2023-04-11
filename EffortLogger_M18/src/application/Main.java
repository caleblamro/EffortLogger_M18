package application;
	
import java.sql.ResultSet;

import database.DatabaseConnection;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			primaryStage.setTitle("Effort Logger");
			VBox root = new VBox();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Text dataPane = new Text();
			root.getChildren().add(dataPane);
			
			primaryStage.setScene(scene);
			primaryStage.show();

			DatabaseConnection c = new DatabaseConnection("jdbc:mysql://localhost:3306/effortlogger", "root", "1Spanky!");
			try {
				System.out.println("Attempting to connect to local server...");
				c.connect();
				System.out.println("Connection created!");
				Thread.sleep(2000);
				System.out.println("Attempting query SELECT * FROM employees");
				ResultSet r = c.executeQuery("select * from employees");
				String data = "";
				while(r.next()) {
					String name = r.getString("name");
					int id = r.getInt("id");
					data += "Name: " + name + " Id: " + id + "\n";
				}
				System.out.println(data);
			}catch(Exception e) {
				System.out.println("Could not connect... \nPrinting error:");
				System.out.println(e);
			}
			c.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
