module FXTest {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.fxml;
	requires jbcrypt;
	requires javafx.base;
	requires org.controlsfx.controls;
	requires java.base;
	
	opens application to javafx.graphics, javafx.fxml;
	opens controllers to javafx.fxml;
	opens entities to javafx.base;
}
