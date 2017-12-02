package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;
import javafx.scene.paint.Color;

public class DrawEdgeInstantly extends DrawEdge {
    public DrawEdgeInstantly(MapEdge edge) {
        super(edge);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        display.drawLine(getId(), getStart().getCoordinate(), getEnd().getCoordinate(), 5, Color.CADETBLUE, true,false);
    }

    public void removeFromScreen(MapDisplay display) {
        display.deleteLine(getId());
    }
}
