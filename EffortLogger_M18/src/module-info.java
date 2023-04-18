module FXTest {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.fxml;
	requires jbcrypt;
	
	opens application to javafx.graphics, javafx.fxml;
}
