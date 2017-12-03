package com.teama.mapsubsystem.data;

import com.teama.mapdrawingsubsystem.MapDisplay;
import javafx.scene.paint.Color;

public class DrawNodeInstantly extends DrawNode {
    public DrawNodeInstantly(MapNode mapNode) {
        super(mapNode);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        display.drawPoint(getId(),getCoordinate(),8, Color.BLACK,false);
    }

    public void displayOnScreen(MapDisplay display, int size, Color color) {
        display.drawPoint(getId(), getCoordinate(), size, color, false);
    }

    @Override
    public void removeFromScreen(MapDisplay display) {
        display.deletePoint(getId());
    }
}
