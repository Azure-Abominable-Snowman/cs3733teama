package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class PopOutController {

    @FXML
    private ImageView arrow; // Arrow that points to the spawn point

    @FXML
    private Pane mainPane;

    @FXML
    private JFXToggleButton viewNodes, viewEdges;

    protected void alignPane(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        xProperty.addListener((ObservableValue<? extends Number> a, Number before, Number after) -> {
            mainPane.setTranslateX(after.doubleValue()+xOffset-arrow.getFitWidth()-mainPane.getPrefWidth()-5); // 5 as a buffer from the button
        });

        yProperty.addListener((ObservableValue<? extends Number> a, Number before, Number after) -> {
            arrow.setTranslateY(after.doubleValue()+yOffset);
            mainPane.setTranslateY(15);  // buffer from the top of the screen
        });

        arrow.setTranslateY(yProperty.getValue()+yOffset);
        mainPane.setTranslateY(15); // buffer from the top of the screen
        mainPane.setTranslateX(xProperty.getValue()+xOffset-arrow.getFitWidth()-mainPane.getPrefWidth()-5); // 5 as a buffer from the button
    }

    public abstract void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset);
    public abstract void onClose();
    public abstract String getFXMLPath();
}
