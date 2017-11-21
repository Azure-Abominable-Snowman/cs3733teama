package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.MapEdge;

public class DisplayPathInstantly extends DisplayPath {
    public DisplayPathInstantly(Path path) {
        super(path);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        /*for(MapNode n : getNodes()) {
            new DrawNodeInstantly(n).displayOnScreen(display);
        }*/
        for(MapEdge e : getConnectors()) {
            System.out.println(e);
            //new DrawEdgeInstantly(e).displayOnScreen(display);
        }
    }

    public void deleteFromScreen(MapDisplay display) {
        /*for(MapNode n : getNodes()) {
            display.deleteLine(n.getId());
        }*/
        for(MapEdge e : getConnectors()) {
            System.out.println(e);
            display.deleteLine(e.getId());
        }
    }
}
