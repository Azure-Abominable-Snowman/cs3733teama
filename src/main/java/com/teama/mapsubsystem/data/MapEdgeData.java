package com.teama.mapsubsystem.data;

import com.teama.mapsubsystem.MapSubsystem;

public abstract class MapEdgeData extends MapEdge {

    public MapEdgeData(String id, String start, String end) {
        super(id, start, end);
    }

    public MapEdgeData(String id, MapNodeData start, MapNodeData end, double weight) {
        super(id, start, end, weight);
    }

    public MapEdgeData(String id, MapNodeData start, MapNodeData end) {
        super(id, start, end);
    }

    public MapEdgeData(String id, String start, String end, double weight) {
        super(id, start, end, weight);
    }

    /**
     * Calculates the weight of the edge using the distance formula
     * @return
     */
    private double calculateWeight() {
        return Math.sqrt(Math.pow(getEnd().getCoordinate().getxCoord()-getStart().getCoordinate().getxCoord(), 2)+
                Math.pow(getEnd().getCoordinate().getyCoord()-getStart().getCoordinate().getyCoord(), 2));
    }

    /**
     * Gets the starting node, this calls the MapSubsystem static class, make sure this is what you want to do
     * TODO: Make this less opaque
     * @return
     */
    public void setID(String id) {
        this.id = id;
    }
    public MapNodeData getStart() {
        if(start == null) {
            start = MapSubsystem.getInstance().getMap().getNode(startID);
        }
        return start;
    }

    public void setStart(MapNodeData start) {
        if(start == null) {
            System.out.println("Tried to set a null start");
            return;
        }
        this.start = start;
        if(end != null) {
            weight = calculateWeight();
        }
    }

    /**
     * Gets the ending node, this calls the MapSubsystem static class, make sure this is what you want to do
     * TODO: Make this less opaque
     * @return
     */
    public MapNodeData getEnd() {
        if(end == null) {
            end = MapSubsystem.getInstance().getMap().getNode(endID);
        }
        return end;
    }

    public void setEnd(MapNodeData end) {
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
        if(weight == -1) {
            weight = calculateWeight();
        }
        return weight;
    }

    public String getStartID() {
        return startID;
    }

    public String getEndID() {
        return endID;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String toCSV() {
        return String.format("\"%s\",\"%s\",\"%s\"", getId(), getStartID(), getEndID());
    }

    public String toSQLVals() {
        return String.format("'%s','%s','%s'", getId(), getStartID(), getEndID());
    }
}
