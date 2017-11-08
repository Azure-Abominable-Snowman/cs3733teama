package entities;

import java.util.ArrayList;

public class MapNode {
    private Location coordinate;
    private String id, shortDescription, longDescription, teamAssignment;
    private Enum<NodeType> nodeType;
    private ArrayList<MapEdge> edges;

    public MapNode(String id, Location coordinate, Enum<NodeType> nodeType, String shortDescription,
                   String longDescription, String teamAssignment, ArrayList<MapEdge> edges) {
        this.coordinate = coordinate;
        this.id = id;
        this.nodeType = nodeType;
        this.edges = edges;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.teamAssignment = teamAssignment;
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
        return "\'"+id+"\',"+coordinate.getxCoord()+","+coordinate.getyCoord()+",\'"+coordinate.getLevel()+"\',\'"+
                coordinate.getBuilding()+"\',\'"+nodeType.name()+"\',\'"+longDescription+"\',\'"+shortDescription+"\',\'"+teamAssignment+"\'";
    }
}
