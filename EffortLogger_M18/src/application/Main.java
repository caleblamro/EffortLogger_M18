package application;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnection;
import entities.Org;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	DatabaseConnection c;
	@Override
	public void start(Stage primaryStage) {
		//wrap all your database calls with this, it will prevent the app from freezing up while waiting for those calls to finish
		CompletableFuture.runAsync(() -> {
			try {	
				//SHOW LOGIN/SIGNUP HERE	
				c = new DatabaseConnection();
				System.out.println("Attempting to connect to local server...");
				c.connect();
				System.out.println("Connection created!");
				Thread.sleep(2000);
				ArrayList<Org> l = c.getOrgs();
				System.out.println("PRINTING ORGS: ");
				for(Org o : l) {
					System.out.println(o);
				}	
			}catch(SQLException e) {
				System.out.println("ERROR IN SQL");
				e.printStackTrace();
			}catch(Exception e) {
				System.out.println("Could not connect... \nPrinting error:");
				e.printStackTrace();
			}
		});
			
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("ManagerDashboard.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Scene scene = new Scene(root,660,460);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Effort Logger");
		primaryStage.setOnCloseRequest(e -> {
			try {
				c.disconnect();
				System.out.println("DISCONNECTED");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
