package entities;

public class MapEdge {
    private MapNode start, end;
    private String id;
    private double weight;

    public MapEdge(String id, MapNode start, MapNode end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.id = id;
    }

    public MapNode getStart() {
        return start;
    }

    public void setStart(MapNode start) {
        this.start = start;
    }

    public MapNode getEnd() {
        return end;
    }

    public void setEnd(MapNode end) {
        this.end = end;
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
        return String.format("'%s','%s','%s'", getId(), start.getId(), end.getId());
    }
}
