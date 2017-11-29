package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;
import javafx.scene.paint.Color;

public class DrawNodeInstantly extends DrawNode {
    public DrawNodeInstantly(MapNode mapNode) {
        super(mapNode);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
<<<<<<< HEAD
        display.drawPoint(getId(),getCoordinate(),9, Color.BLACK,false);
=======
        display.drawPoint(getId(),getCoordinate(),8, Color.BLACK,false);
    }

    public void displayOnScreen(MapDisplay display, int size, Color color) {
        display.drawPoint(getId(), getCoordinate(), size, color, false);
>>>>>>> debd930a63094ba67f16d534fdf245a250150458
    }

    @Override
    public void removeFromScreen(MapDisplay display) {
        display.deletePoint(getId());
    }
}
