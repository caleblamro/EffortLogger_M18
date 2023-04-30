package ui;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.TitledPane;
import entities.Defect;
import entities.Employee;
import entities.Project;
import entities.Team;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import application.Main;

public class DefectList extends TitledPane {
    private TableView<Defect> tableView;
	public DefectList(String title, ArrayList<Defect> teamList) {
        // Create a table view to display the teams
        tableView = new TableView<Defect>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Create columns for the team data
        TableColumn<Defect, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory((cellData) -> {
            Integer teamId = cellData.getValue().getID();
            return new SimpleObjectProperty<Integer>(teamId);
        });

        TableColumn<Defect, String> descriptionColumn = new TableColumn<Defect, String>("Description");
        descriptionColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getDescription()));
        descriptionColumn.setCellFactory(column -> {
            TableCell<Defect, String> cell = new TableCell<Defect, String>() {
                private Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setFill(Color.web("#ffffff"));
                        text.setText(item);
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        TableColumn<Defect, String> managerColumn = new TableColumn<Defect, String>("Project");
        managerColumn.setCellValueFactory(cellData -> {
        	SimpleStringProperty s = null;
			try {
				s = new SimpleStringProperty(Main.c.getProject(cellData.getValue().getProjectId()).getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return s;
		});
        // Populate the table view with the team data
        tableView.getColumns().addAll(idColumn, descriptionColumn, managerColumn);
        // Populate the table view with the team data
        ObservableList<Defect> observableList = FXCollections.observableArrayList(teamList);
        tableView.setItems(observableList);
        Text text = new Text(title);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        text.setFill(Color.web("#ffffff"));
        setGraphic(text);
        // Set the content of the titled pane to be the table view
        tableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);
        VBox container = new VBox(tableView);
        VBox.setVgrow(tableView, Priority.NEVER);
        setContent(container);
    }
	public void update(ArrayList<Defect> u) {
	    tableView.getItems().clear();
	    tableView.getItems().addAll(u);
	}
}
