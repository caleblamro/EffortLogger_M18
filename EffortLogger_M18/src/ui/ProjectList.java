package ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.TitledPane;
import entities.Employee;
import entities.Project;
import java.util.ArrayList;
import java.sql.Date;

public class ProjectList extends TitledPane {
    private TableView<Project> tableView;
	public ProjectList(String title, ArrayList<Project> teamList) {
        // Create a table view to display the teams
        tableView = new TableView<Project>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns for the team data
        TableColumn<Project, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory((cellData) -> {
            Integer teamId = cellData.getValue().getID();
            return new SimpleObjectProperty<Integer>(teamId);
        });

        TableColumn<Project, String> nameColumn = new TableColumn<Project, String>("Name");
        nameColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getName()));

        TableColumn<Project, String> descriptionColumn = new TableColumn<Project, String>("Description");
        descriptionColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getDescription()));
        descriptionColumn.setCellFactory(column -> {
            TableCell<Project, String> cell = new TableCell<Project, String>() {
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

        TableColumn<Project, Date> startColumn = new TableColumn<Project, Date>("Start Date " + System.lineSeparator() + "(yyyy-mm-dd)");
        startColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getStartDate()));

        TableColumn<Project, Date> estEndColumn = new TableColumn<Project, Date>("Projected End Date"+ System.lineSeparator() +" (yyyy-mm-dd)");
        estEndColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getEndDate()));

        TableColumn<Project, Date> endColumn = new TableColumn<Project, Date>("Actual End Date"+ System.lineSeparator() +" (yyyy-mm-dd)");
        endColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getActualEndDate() == null ? "N/A" : cellData.getValue().getActualEndDate()));

        tableView.getColumns().addAll(nameColumn, idColumn, descriptionColumn, startColumn, estEndColumn, endColumn);

        // Populate the table view with the team data
        ObservableList<Project> observableList = FXCollections.observableArrayList(teamList);
        tableView.setItems(observableList);
        Text text = new Text(title);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        text.setFill(Color.web("#ffffff"));
        setGraphic(text);
        VBox container = new VBox(tableView);
        container.setVgrow(tableView, Priority.NEVER);
        setContent(container);
    }
	public void update(ArrayList<Project> u) {
	    tableView.getItems().clear();
	    tableView.getItems().addAll(u);
	}
}