package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.DrawEdgeInstantly;
import com.teama.mapsubsystem.data.Floor;
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
            new DrawEdgeInstantly(e).displayOnScreen(display);
        }
    }

    @Override
    public void displayOnScreen(MapDisplay display, Floor floor) {
        for(MapEdge e : getConnectors()) {
            if(e.getStart().getCoordinate().getLevel().equals(floor) &&
                    e.getEnd().getCoordinate().getLevel().equals(floor))
                new DrawEdgeInstantly(e).displayOnScreen(display);
        }
    }

    public void deleteFromScreen(MapDisplay display) {
        /*for(MapNode n : getNodes()) {
            display.deleteLine(n.getId());
        }*/
        for(MapEdge e : getConnectors()) {
            display.deleteLine(e.getId());
        }
    }
}
