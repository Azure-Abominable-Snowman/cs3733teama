package com.teama.controllers_refactor;

import com.teama.ProgramSettings;
import com.teama.mapsubsystem.pathfinding.DirectionAdapter;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextualDirections;
import javafx.beans.property.ReadOnlyDoubleProperty;
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

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

        putDirectionsOnScreen(ProgramSettings.getInstance().getCurrentDisplayedPathProp().getValue());
        ProgramSettings.getInstance().getCurrentDisplayedPathProp().addListener((a, b, currentPath) -> {
            putDirectionsOnScreen(currentPath);
        });

        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        distanceCol.setCellValueFactory(
                new PropertyValueFactory<>("distance"));

        textDirections.setFixedCellSize(75); // cells need to be bigger than default

        // Factory for each row, set to have the text wrap
        textDirections.setRowFactory(tv -> {
            TableRow<DirectionAdapter> row = new TableRow<>();
            row.setWrapText(true);
            return row;
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
