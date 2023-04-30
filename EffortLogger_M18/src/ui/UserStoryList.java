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
import entities.Team;
import entities.UserStory;

import java.util.ArrayList;
import java.util.stream.Collectors;

import application.Main;

import java.sql.Date;

public class UserStoryList extends TitledPane {
    private TableView<UserStory> tableView;
	public UserStoryList(String title, ArrayList<UserStory> teamList) {
        // Create a table view to display the teams
        tableView = new TableView<UserStory>();

        // Create columns for the team data
        TableColumn<UserStory, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory((cellData) -> {
            Integer teamId = cellData.getValue().getID();
            return new SimpleObjectProperty<Integer>(teamId);
        });

        TableColumn<UserStory, String> nameColumn = new TableColumn<UserStory, String>("Name");
        nameColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getName()));

        TableColumn<UserStory, String> descriptionColumn = new TableColumn<UserStory, String>("Description");
        descriptionColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getDescription()));
        descriptionColumn.setCellFactory(column -> {
            TableCell<UserStory, String> cell = new TableCell<UserStory, String>() {
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

        TableColumn<UserStory, Date> startColumn = new TableColumn<UserStory, Date>("Start Date " + System.lineSeparator() + "(yyyy-mm-dd)");
        startColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getStartDate()));

        TableColumn<UserStory, Date> estEndColumn = new TableColumn<UserStory, Date>("Projected End Date"+ System.lineSeparator() +" (yyyy-mm-dd)");
        estEndColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getEstEndDate()));

        TableColumn<UserStory, Date> endColumn = new TableColumn<UserStory, Date>("Date Completed Date"+ System.lineSeparator() +" (yyyy-mm-dd)");
        endColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getActualEndDate() == null ? "N/A" : cellData.getValue().getActualEndDate()));

        TableColumn<UserStory, String> sprintColumn = new TableColumn<>("Team ID");
        sprintColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTeam() + ""));

        TableColumn<UserStory, String> pointsColumn = new TableColumn<UserStory, String>("Story Points");
        pointsColumn.setCellValueFactory((obj) -> new SimpleStringProperty(obj.getValue().getStoryPoints() + ""));
        
        tableView.getColumns().addAll(nameColumn, idColumn, descriptionColumn, startColumn, estEndColumn, endColumn, sprintColumn, pointsColumn);

        // Populate the table view with the team data
        ObservableList<UserStory> observableList = FXCollections.observableArrayList(teamList);
        tableView.setItems(observableList);
        Text text = new Text(title);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        text.setFill(Color.web("#ffffff"));
        setGraphic(text);
        // Set the content of the titled pane to be the table view
        tableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);
        VBox container = new VBox(tableView);
        container.setVgrow(tableView, Priority.NEVER);
        setContent(container);
    }
	public void update(ArrayList<UserStory> u) {
	    tableView.getItems().clear();
	    tableView.getItems().addAll(u);
	}
}