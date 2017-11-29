package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.*;

import java.util.ArrayList;

public class GenerateTextTestMap {

    ArrayList<MapNode> map1 ;
    ArrayList<MapNode> map2 ;

    GenerateTextTestMap()
    {
        map1 = new ArrayList<>();
        map2 = new ArrayList<>();

    }


    public void makeMap()
    {

    }

    public void createNode(String cords , Floor floor)
    {

    }















    public void linkNodes(MapNodeData node1, MapNodeData node2, double weight){
        String temp = String.format("%s - %s",node1.getId(),node2.getId());
        MapEdgeData edge = new MapEdgeData(temp, node1, node2, weight);
        node1.addEdge(edge);
        node2.addEdge(edge);
    }
}
