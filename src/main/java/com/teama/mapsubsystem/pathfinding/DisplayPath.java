package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;

// TODO: turn this into a real decorator pattern! (refactor after algorithms is done)
public abstract class DisplayPath {

    Path path;

    public DisplayPath(Path path) {
        this.path = path;
    }

    public void addNode(MapNode n) {
        path.addNode(n);
    }

    public void addEdge(MapEdge e) {
        path.addEdge(e);
    }

    public ArrayList<MapNode> getNodes() {
        return path.getNodes();
    }

    public ArrayList<MapEdge> getConnectors() {
        return path.getConnectors();
    }

    public abstract void displayOnScreen(MapDisplay display);
    public abstract void displayOnScreen(MapDisplay display, Floor floor);
    public abstract void deleteFromScreen(MapDisplay display);
}
