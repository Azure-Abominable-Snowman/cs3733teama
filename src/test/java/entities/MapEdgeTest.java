package entities;

import org.junit.Test;

public class MapEdgeTest {
    MapEdge testEdge;
    MapNode nodeOne, nodeTwo;

    public MapEdgeTest() {
        // Create a new edge with known values and nodes.
        //'WHALL00203',2080,1290,'3','45 Francis','HALL','Hallway Connector 2 Floor 3','Hallway W0203','Team I'
        //'WHALL00302',2085,900,'2','45 Francis','HALL','Hallway Connector 3 Floor 2','Hallway W0302','Team H'
        nodeOne = new MapNode("WHALL00203", new Location(2080, 1280, "3", "45 Francis"),
                NodeType.HALL, "Hallway W0203", "Hallway Connector 2 Floor 3", "Team I", null);
        nodeTwo = new MapNode("WHALL00302", new Location(2080, 900, "2", "45 Francis"),
                NodeType.HALL, "Hallway W0302", "Hallway Connector 3 Floor 2", "Team H", null);
        testEdge = new MapEdge("TestEdge", nodeOne, nodeTwo, 0.5);
    }

    @Test
    public void getStart() throws Exception {
        assert testEdge.getStart().toCSV().equals(nodeOne.toCSV());
    }

    @Test
    public void setStart() throws Exception {
        testEdge.setStart(nodeTwo);
        assert testEdge.getStart().toCSV().equals(nodeTwo.toCSV());
        testEdge.setStart(nodeOne);
    }

    @Test
    public void getEnd() throws Exception {
        assert testEdge.getEnd().toCSV().equals(nodeTwo.toCSV());
    }

    @Test
    public void setEnd() throws Exception {
        testEdge.setEnd(nodeOne);
        assert testEdge.getStart().toCSV().equals(nodeOne.toCSV());
        testEdge.setEnd(nodeTwo);
    }

    @Test
    public void getWeight() throws Exception {
        assert testEdge.getWeight() == 0.5;
    }

    @Test
    public void setWeight() throws Exception {
    }

    @Test
    public void getId() throws Exception {
        assert testEdge.getId().equals("TestEdge");
    }

    @Test
    public void toCSV() throws Exception {
        assert testEdge.toCSV().equals("'TestEdge','WHALL00203','WHALL00302'");
    }

}