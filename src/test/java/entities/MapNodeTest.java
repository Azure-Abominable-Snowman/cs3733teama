package entities;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MapNodeTest {
    private MapNode nodeOne;
    public MapNodeTest() {
        nodeOne = new MapNode("WHALL00203", new Location(2080, 1280, "3", "45 Francis"),
                NodeType.HALL, "Hallway Connector 2 Floor 3", "Hallway W0203", "Team I", null);
    }

    @Test
    public void toCSV() throws Exception {
        assertEquals(nodeOne.toCSV(),
                "\"WHALL00203\",2080,1280,\"3\",\"45 Francis\",\"HALL\",\"Hallway Connector 2 Floor 3\",\"Hallway W0203\",\"Team I\"");
    }

    @Test
    public void toSQLVals() throws Exception {
        assertEquals(nodeOne.toSQLVals(),
                "'WHALL00203',2080,1280,'3','45 Francis','HALL','Hallway Connector 2 Floor 3','Hallway W0203','Team I'");
    }

}