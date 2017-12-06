package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXComboBox;
import com.teama.ProgramSettings;
import com.teama.controllers.SearchBarController;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionAdapter;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextualDirections;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class DirectionsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    @FXML
    private TableView<DirectionAdapter> textDirections;

    @FXML
    private TableColumn<String, Image> imageCol;

    @FXML
    private TableColumn<String, String> descriptionCol;

    @FXML
    private TableColumn<String, String> distanceCol;

    @FXML
    private JFXComboBox<String> originNodeCombo;

    @FXML
    private JFXComboBox<String> filterBox;

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

        ReadOnlyObjectProperty<Path> pathObjectProperty = ProgramSettings.getInstance().getCurrentDisplayedPathProp();

        if(pathObjectProperty.getValue() != null) {
            putDirectionsOnScreen(pathObjectProperty.getValue());
        }
        pathObjectProperty.addListener((a, b, currentPath) -> {
            putDirectionsOnScreen(currentPath);
        });

        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        distanceCol.setCellValueFactory(
                new PropertyValueFactory<>("distance"));
        imageCol.setCellValueFactory(
                new PropertyValueFactory<>("image"));


        textDirections.setFixedCellSize(100); // cells need to be bigger than default

        // Factory for each row, set to have the text wrap
        textDirections.setRowFactory(tv -> {
            TableRow<DirectionAdapter> row = new TableRow<>();
            row.setWrapText(true);
            return row;
        });

        // Populate the combo box and allow fuzzy search by tying it to a search controller
        SearchBarController searchBarController = new SearchBarController(originNodeCombo, true);

        // When the origin combo box changes selected value, set the origin of the path to the new one
        originNodeCombo.getSelectionModel().selectedItemProperty().addListener((Observable a) -> {
            System.out.println("CHANGED SELECTION "+originNodeCombo.getSelectionModel().getSelectedItem());
            MapNode selectedNode = MapSubsystem.getInstance().getNodeByDescription(originNodeCombo.getSelectionModel().getSelectedItem(), true);
            if(selectedNode != null) {
                // Tell path controller to make a new path
                ProgramSettings.getInstance().setPathOriginNodeProp(selectedNode);
            }
        });
    }

    private DirectionsGenerator directionsGenerator = new TextualDirections();

    private void putDirectionsOnScreen(Path path) {
        TextDirections directions = directionsGenerator.generateDirections(path);
        ObservableList<DirectionAdapter> directionVals = FXCollections.observableArrayList();
        for(Direction d : directions.getDirections()) {
            directionVals.add(new DirectionAdapter(d));
        }
        textDirections.setItems(directionVals);

        // make the combo box automatically change to the start of the path
        originNodeCombo.getEditor().setText(path.getStartNode().getLongDescription());
    }

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {

    }

    @Override
    public String getFXMLPath() {
        return "/DirectionsPopOut.fxml";
    }
}
