package com.teama.mapsubsystem.data;

import java.util.ArrayList;

public abstract class MapNodeData extends MapNode{


    public MapNodeData(String id, Location coordinate, Enum<NodeType> nodeType, String longDescription, String shortDescription, String teamAssignment) {
        super(id, coordinate, nodeType, longDescription, shortDescription, teamAssignment);
    }

    public MapNodeData(String id, Location coordinate, Enum<NodeType> nodeType, String longDescription, String shortDescription, String teamAssignment, ArrayList<MapEdge> edges) {
        super(id, coordinate, nodeType, longDescription, shortDescription, teamAssignment, edges);
    }

    public void setID(String id) {
        this.id = id;
    }
    public void setType(NodeType n) {
        nodeType = n;
    }
    public Location getCoordinate() {
        return coordinate;
    }

    public String getId() {
        return id;
    }

    public Enum<NodeType> getNodeType() {
        return nodeType;
    }

    public ArrayList<MapEdge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<MapEdge> edges) {
        this.edges = edges;
    }

    public void addEdge(MapEdgeData edge) {
        this.edges.add(edge);
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getTeamAssignment() {
        return teamAssignment;
    }

    public String toCSV() {
        return "\""+id+"\","+coordinate.getxCoord()+","+coordinate.getyCoord()+",\""+coordinate.getLevel()+"\",\""+
                coordinate.getBuilding()+"\",\""+nodeType.name()+"\",\""+longDescription+"\",\""+shortDescription+"\",\""+teamAssignment+"\"";
    }

    public String toSQLVals() {
        return "'"+id+"',"+coordinate.getxCoord()+","+coordinate.getyCoord()+",'"+coordinate.getLevel()+"','"+
                coordinate.getBuilding()+"','"+nodeType.name()+"','"+longDescription+"','"+shortDescription+"','"+teamAssignment+"'";
    }

    public String toString() {
        return getShortDescription();
    }
}
