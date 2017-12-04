package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXTextArea;
import com.teama.controllers.Settings;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextualDirections;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;

public class DirectionsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    @FXML
    private JFXTextArea textDirections;

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

        putDirectionsOnScreen(Settings.getInstance().getCurrentDisplayedPathProp().getValue());
        Settings.getInstance().getCurrentDisplayedPathProp().addListener((a, b, currentPath) -> {
            putDirectionsOnScreen(currentPath);
        });
    }

    private DirectionsGenerator directionsGenerator = new TextualDirections();
    private void putDirectionsOnScreen(Path path) {
        if(path != null) {
            TextDirections directions = directionsGenerator.generateDirections(path);
            textDirections.clear();
            for (Direction direction : directions.getDirections()) {
                textDirections.appendText(direction.getDescription());
            }
        }
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
