package entities;

public class MapEdge {
    private MapNode start, end;
    private String id;
    private double weight;

    /**
     * Creates an edge on the graph, the weight is assumed to be the euclidean distance between the start and end nodes.
     *
     * @param id
     * @param start
     * @param end
     */
    public MapEdge(String id, MapNode start, MapNode end) {
        this(id, start, end,
                Math.sqrt(Math.pow(end.getCoordinate().getxCoord() - start.getCoordinate().getxCoord(), 2) +
                        Math.pow(end.getCoordinate().getyCoord() - start.getCoordinate().getyCoord(), 2)));
    }

    public MapEdge(String id, MapNode start, MapNode end, double weight) {
        if (start == null) {
            System.out.println("Tried to set a null start");
        }
        if (end == null) {
            System.out.println("Tried to set a null end");
        }
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.id = id;
    }

    public MapNode getStart() {
        return start;
    }

    public void setStart(MapNode start) {
        if (start == null) {
            System.out.println("Tried to set a null start");
        }
        this.start = start;
    }

    public MapNode getEnd() {

        return end;
    }

    public void setEnd(MapNode end) {
        if (end == null) {
            System.out.println("Tried to set a null end");
        }
        this.end = end;
    }

    public MapNode getAdjacentNode(MapNode originalNode) {
        if (originalNode == this.start) return this.end;
        else return this.start;
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
