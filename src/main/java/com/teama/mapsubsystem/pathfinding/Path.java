package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;

public class Path {
    private ArrayList<MapNode> nodes;
    private ArrayList<MapEdge> connectors;
    private MapNode startNode; // @TODO do we need this? since the start of Arraylist could be start node.
    private MapEdge endNode;

    public Path()
    {
        nodes = new ArrayList<MapNode>();
        connectors = new ArrayList<MapEdge>();
    }

    public boolean addNode(MapNode node)
    {
        if (!nodes.contains(node)) {
            nodes.add(node);
            return true;
        }
        return false;
    }
    public boolean addEdge(MapEdge edge)
    {
        if (!connectors.contains(edge)) {
            connectors.add(edge);
            return true;
        }
        return false;
    }

    public ArrayList<MapEdge> getConnectors() {
        return connectors;
    }
    public ArrayList<MapNode> getNodes() {
        return nodes;
    }
}
