package entities;

import com.teama.mapsubsystem.data.*;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapEdgeTest {



    private MapEdgeData testEdge;
    private MapNodeData nodeOne, nodeTwo, nodeThree, nodeFour, nodeFive;

    public MapEdgeTest() {
        // Create a new edge with known values and nodes.
        //'WHALL00203',2080,1290,'3','45 Francis','HALL','Hallway Connector 2 Floor 3','Hallway W0203','Team I'
        //'WHALL00302',2085,900,'2','45 Francis','HALL','Hallway Connector 3 Floor 2','Hallway W0302','Team H'
        nodeOne = new MapNodeData("WHALL00203", new Location(2080, 1280, Floor.THREE, "45 Francis"),
                NodeType.HALL, "Hallway W0203", "Hallway Connector 2 Floor 3", "Team I", null);
        nodeTwo = new MapNodeData("WHALL00302", new Location(2070, 900, Floor.TWO, "45 Francis"),
                NodeType.HALL, "Hallway W0302", "Hallway Connector 3 Floor 2", "Team H", null);
        nodeThree = new MapNodeData("WHALL00302", new Location(2010, 700, Floor.TWO, "45 Francis"),
                NodeType.HALL, "Hallway W0303", "Hallway Connector 3 Floor 3", "Team Z", null);

        nodeFour = new MapNodeData("WHALL00304", new Location(2010, 700, Floor.TWO, "45 Francis"),
                NodeType.HALL, "Hallway W0304", "Hallway Connector 3 Floor 4", "Team Z", null);
        nodeFive = new MapNodeData("WELEV00W03", new Location(2080, 1280, Floor.THREE, "45 Francis"),
                NodeType.HALL, "Elevator W0203", "Elevator W Floor 3", "Team I", null);

        testEdge = new MapEdgeData("TestEdge", nodeOne, nodeTwo);
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
        assertEquals(testEdge.getWeight(), 380.13, 0.1); // One to two
        testEdge.setEnd(nodeThree);
        assertEquals(testEdge.getWeight(), 584.208, 0.1); // One to three
        testEdge.setEnd(nodeTwo);
        assertEquals(testEdge.getWeight(), 380.13, 0.1); //  One to two
        testEdge.setStart(nodeTwo);
        assertEquals(testEdge.getWeight(), 0, 0); // Two to two
        testEdge.setStart(nodeOne);
    }

    @Test
    public void setWeight() throws Exception {
        testEdge.setWeight(5.0);
        assertEquals(testEdge.getWeight(), 5.0);
        testEdge.setStart(nodeOne);
        assertEquals(testEdge.getWeight(),380.13, 0.1); // recalculate after node changes
    }

    @Test
    public void getId() throws Exception {
        assert testEdge.getId().equals("TestEdge");
    }

    @Test
    public void toCSV() throws Exception {
        assert testEdge.toCSV().equals("\"TestEdge\",\"WHALL00203\",\"WHALL00302\"");
    }

    @Test
    public void toSQLVals() throws Exception {
        assert testEdge.toSQLVals().equals("'TestEdge','WHALL00203','WHALL00302'");
    }

    @Test
    public void doesNotCrossFloors() throws Exception {
        MapEdgeData sameFloor = new MapEdgeData("WHALL00203_WELEV00W03", nodeOne, nodeFive);
        MapEdgeData diffFloor = new MapEdgeData("WHALL00304_WELEV00W03", nodeFour,nodeFive);
        assertTrue(sameFloor.doesNotCrossFloors());
        assertFalse(diffFloor.doesNotCrossFloors());

    }

    @Test
    public void isOnFloor() throws Exception {
        MapEdgeData sameFloor = new MapEdgeData("WHALL00203_WELEV00W03", nodeOne, nodeFive);
        assertTrue(sameFloor.isOnFloor("03"));
        assertFalse(sameFloor.isOnFloor("0G"));

    }
}