package entities.PathRelated;

import entities.MapEdge;
import entities.MapNode;
import java.util.ArrayList;

public class Path {
    private ArrayList<MapNode> nodes;
    private ArrayList<MapEdge> connectors;
    private MapNode startNode; // @TODO do we need this? since the start of Arraylist could be start node. 
    private MapNode endNode;

    Path ()
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
        if (connectors.contains(edge)) {
            connectors.add(edge);
            return true;
        }
        return false ;
    }

    public ArrayList<MapEdge> getConnectors() {
        return connectors;
    }
    public ArrayList<MapNode> getNodes() {
        return nodes;
    }
}
