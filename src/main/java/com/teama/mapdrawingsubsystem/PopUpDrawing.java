package com.teama.mapdrawingsubsystem;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Created by aliss on 12/4/2017.
 */
public abstract class PopUpDrawing {
    @FXML
    public Pane bgPane;

    private AnchorPane areaPane;

    public void generatePopUp(double sceneX, double sceneY, double xOffset, double yOffset) {
        // Get the node clicked on (if any)

        areaPane = MapDrawingSubsystem.getInstance().getAreaPane();
        bgPane.toFront(); // bring to front of screen


        bgPane.widthProperty().addListener((ObservableValue<? extends Number> a, Number b, Number after) -> {
            double newX = sceneX-(after.doubleValue()/2)+xOffset;
            if(newX <= areaPane.getWidth()-bgPane.getBoundsInParent().getWidth() && newX >= 0) {
                bgPane.setTranslateX(newX);
            } else if (newX > 0) {
                bgPane.setTranslateX(areaPane.getWidth()-bgPane.getBoundsInParent().getWidth()*1.2);
            }
        });

        bgPane.heightProperty().addListener((ObservableValue<? extends Number> a, Number b, Number after) -> {
            double newY = sceneY-after.doubleValue()+yOffset;
            System.out.println("TRANS Y "+after.doubleValue());
            if(newY <= areaPane.getHeight() && newY >= 0) {
                System.out.println("MOVE Y");
                bgPane.setTranslateY(newY);
            }
        });
    }
}
