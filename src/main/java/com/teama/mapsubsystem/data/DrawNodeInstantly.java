package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;
import javafx.scene.paint.Color;

public class DrawNodeInstantly extends DrawNode {
    public DrawNodeInstantly(MapNode mapNode) {
        super(mapNode);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        display.drawPoint(getId(),getCoordinate(),4, Color.BLACK,false);
    }
}
