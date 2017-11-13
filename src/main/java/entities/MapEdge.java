package entities;

import java.util.ArrayList;
import java.util.List;

public class MapEdge {
    private MapNode start, end;
    private String id;
    private double weight;

    /**
     * Creates an edge on the graph, the weight is assumed to be the euclidean distance between the start and end nodes.
     * @param id
     * @param start
     * @param end
     */
    public MapEdge(String id, MapNode start, MapNode end) {
        setStart(start);
        setEnd(end);
        this.id = id;
        this.weight = calculateWeight();
    }

    /**
     * Creates an edge using a defined weight
     * @param id
     * @param start
     * @param end
     * @param weight
     */
    public MapEdge(String id, MapNode start, MapNode end, double weight) {
        setStart(start);
        setEnd(end);
        this.id = id;
        this.weight = weight;
    }

    /**
     * Calculates the weight of the edge using the distance formula
     * @return
     */
    private double calculateWeight() {
        return Math.sqrt(Math.pow(end.getCoordinate().getxCoord()-start.getCoordinate().getxCoord(), 2)+
                Math.pow(end.getCoordinate().getyCoord()-start.getCoordinate().getyCoord(), 2));
    }

    public MapNode getStart() {
        return start;
    }

    public void setStart(MapNode start) {
        if(start == null) {
            System.out.println("Tried to set a null start");
            return;
        }
        this.start = start;
        if(end != null) {
            weight = calculateWeight();
        }
    }

    public MapNode getEnd() {
        return end;
    }

    public void setEnd(MapNode end) {
        if(end == null) {
            System.out.println("Tried to set a null end");
            return;
        }
        this.end = end;
        if(start != null) {
            weight = calculateWeight();
        }
    }
    // returns true  if edge is on input floor (either start or end node is on the floor)
    public boolean isOnFloor(String floor) {
        String[] connections = id.split("_");
        String edgeStart = connections[0];
        String edgeEnd = connections[1];


        String floorStart = edgeStart.substring(edgeStart.length()-2, edgeStart.length());
        String floorEnd = edgeEnd.substring(edgeEnd.length()-2, edgeEnd.length());
        return (floorStart.equals(floor) || floorEnd.equals(floor));
    }

    public boolean doesNotCrossFloors() {
        String[] connections = id.split("_");
        String edgeStart = connections[0];
        String edgeEnd = connections[1];


        String floorStart = edgeStart.substring(edgeStart.length()-2, edgeStart.length());
        String floorEnd = edgeEnd.substring(edgeEnd.length()-2, edgeEnd.length());

        return floorStart.equals(floorEnd);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String toCSV() {
        return String.format("\"%s\",\"%s\",\"%s\"", getId(), start.getId(), end.getId());
    }

    public String toSQLVals() {
        return String.format("'%s','%s','%s'", getId(), start.getId(), end.getId());
    }
}
