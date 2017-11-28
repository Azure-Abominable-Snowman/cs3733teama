package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;
import javafx.scene.paint.Color;

public class DrawEdgeInstantly extends DrawEdge {
    public DrawEdgeInstantly(MapEdge edge) {
        super(edge);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
<<<<<<< HEAD
        display.drawLine(getId(), getStart().getCoordinate(), getEnd().getCoordinate(), 7, Color.CADETBLUE, false);
=======
        display.drawLine(getId(), getStart().getCoordinate(), getEnd().getCoordinate(), 5, Color.CADETBLUE, true,false);
>>>>>>> debd930a63094ba67f16d534fdf245a250150458
    }

    public void removeFromScreen(MapDisplay display) {
        display.deleteLine(getId());
    }
}
