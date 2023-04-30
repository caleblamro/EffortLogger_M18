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
import entities.Employee;
import entities.Project;
import entities.Team;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TeamList extends TitledPane {
    private TableView<Team> tableView;
	public TeamList(String title, ArrayList<Team> teamList) {
        // Create a table view to display the teams
        tableView = new TableView<Team>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Create columns for the team data
        TableColumn<Team, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory((cellData) -> {
            Integer teamId = cellData.getValue().getID();
            return new SimpleObjectProperty<Integer>(teamId);
        });

        TableColumn<Team, String> nameColumn = new TableColumn<Team, String>("Name");
        nameColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getName()));

        TableColumn<Team, String> descriptionColumn = new TableColumn<Team, String>("Description");
        descriptionColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getDescription()));
        descriptionColumn.setCellFactory(column -> {
            TableCell<Team, String> cell = new TableCell<Team, String>() {
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

        TableColumn<Team, String> managerColumn = new TableColumn<Team, String>("Manager");
        managerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManager().getName() + ""));
        TableColumn<Team, ArrayList<Employee>> employeeColumn = new TableColumn<>("Employees");
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employees"));
        employeeColumn.setCellFactory(column -> new TableCell<Team, ArrayList<Employee>>(){
        	private Text text = new Text();
            @Override
            protected void updateItem(ArrayList<Employee> employees, boolean empty) {
                super.updateItem(employees, empty);

                if (empty || employees == null || employees.isEmpty()) {
                    setText(null);
                } else {
                    String employeeNames = employees.stream().map(Employee::getName).collect(Collectors.joining(", "));
                    text.setFill(Color.web("#ffffff"));
                    text.setText(employeeNames);
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                    setGraphic(text);
                }
            }
        });
        // Populate the table view with the team data
        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, managerColumn, employeeColumn);
        // Populate the table view with the team data
        ObservableList<Team> observableList = FXCollections.observableArrayList(teamList);
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
	public void update(ArrayList<Team> u) {
	    tableView.getItems().clear();
	    tableView.getItems().addAll(u);
	}
}