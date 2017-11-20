package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;

public class DrawEdgeInstantly extends DrawEdge {
    public DrawEdgeInstantly(MapEdge edge) {
        super(edge);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        System.out.println("DISPLAY EDGE INSTANTLY");
    }
}
