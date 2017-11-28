package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Path {
    private ArrayList<MapNode> nodes;
    private ArrayList<MapEdge> connectors;

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

    public MapNode getStartNode() {
        return getNodes().get(0);
    }

    public MapNode getEndNode() {
        return getNodes().get(getNodes().size()-1);
    }

    public ArrayList<MapEdge> getConnectors() {
        return connectors;
    }
    public ArrayList<MapNode> getNodes() {
        return nodes;
    }

    public Set<Floor> getFloorsCrossed() {
        Set<Floor> floors = new HashSet<>();
        for(MapNode n : getNodes()) {
            floors.add(n.getCoordinate().getLevel());
        }
        return floors;
    }

    public Set<Floor> getFloorsCrossedExceptTrans() {
        Set<Floor> floors = new HashSet<>();
        for(MapNode n : getNodes()) {
            if(!n.getNodeType().equals(NodeType.ELEV) && !n.getNodeType().equals(NodeType.STAI)) {
                floors.add(n.getCoordinate().getLevel());
            }
        }
        return floors;
    }
}
