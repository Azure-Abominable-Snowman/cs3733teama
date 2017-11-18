package com.teama.mapsubsystem.data;

public abstract class MapEdge {

    MapNode start, end;
    String startID, endID;
    String id;
    double weight = -1;
    private DrawEdge animationStyle;

    /**
     * Creates an edge on the graph, the weight is assumed to be the euclidean distance between the start and end nodes.
     * @param id
     * @param start
     * @param end
     */
    public MapEdge(String id, String start, String end) {
        this.startID = start;
        this.endID = end;
        this.id = id;
    }

    public MapEdge(String id, MapNode start, MapNode end, double weight) {
        this(id, start, end);
        this.weight = weight;
    }

    public MapEdge(String id, MapNode start, MapNode end) {
        this.startID = start.getId();
        this.endID = end.getId();
        this.start = start;
        this.end = end;
        this.id = id;
    }

    public MapEdge(String id, String start, String end, double weight) {
        this.startID = start;
        this.endID = end;
        this.id = id;
        this.weight = weight;
    }

    public abstract void setID(String id);
    public abstract MapNodeData getStart();
    public abstract void setStart(MapNodeData start);
    public abstract MapNodeData getEnd();
    public abstract void setEnd(MapNodeData end);
    public abstract boolean isOnFloor(String floor);
    public abstract boolean doesNotCrossFloors();
    public abstract double getWeight();
    public abstract String getStartID();
    public abstract String getEndID();
    public abstract void setWeight(double weight);
    public abstract String getId();
    public abstract String toCSV();
    public abstract String toSQLVals();

    public void setAnimationStyle(DrawEdge animationStyle) {

    }
}
