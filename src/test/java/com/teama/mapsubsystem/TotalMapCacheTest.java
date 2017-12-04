package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TotalMapCacheTest {
    private MapDataSource testCache;
    private MapDataSource javaDBSource;
    private MapDataSource csvSource;
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String nodeTable, edgeTable;
    private MapNodeData testNode, testNodeChange, n1, n2;
    private MapEdgeData testEdge, testEdgeChange;

    private ArrayList<MapNode> nodesToTest = new ArrayList<>();

    public TotalMapCacheTest() {
        nodeTable = "TEST_CACHE_NODETABLE";
        edgeTable = "TEST_CACHE_EDGETABLE";

        Set<String> nList = new HashSet<>();
        Set<String> eList = new HashSet<>();

        for (char alphabet = 'A'; alphabet <= 'I'; alphabet++) {
            nList.add("/csvdata/nodes/Map"+alphabet+"nodes.csv");
            eList.add("/csvdata/edges/Map"+alphabet+"edges.csv");
        }
        nList.add("/csvdata/nodes/MapWnodes.csv");
        eList.add("/csvdata/edges/MapWedges.csv");
        javaDBSource = new JavaDatabaseSource(dbURL, nodeTable, edgeTable);
        //TODO uncomment if the is fresh run
        csvSource = new CSVDatabaseSource(nList, eList, null, null);
        javaDBSource.addAll(csvSource);
    }

    @Before
    public void setUp() throws Exception {

        testCache = new TotalMapCache(javaDBSource);
        nodesToTest.addAll(javaDBSource.getNodesOnFloor(Floor.ONE.toString()));
    }

    @After
    public void tearDown() throws Exception {
        testCache.close();
    }

    @Test
    public void getNode() throws Exception {
        //id FINFO00101 short des: Lobby Info Desk, long des: 75 Lobby Information Desk
        String db = javaDBSource.getNode("FINFO00101").getLongDescription();
        String cache = testCache.getNode("FINFO00101").getLongDescription();
        assertEquals(db, cache);
    }

    @Test
    public void getNodeByDescription() throws Exception {
        String db =  javaDBSource.getNode("75 Lobby Information Desk", true).getId();
        String cache = testCache.getNode("75 Lobby Information Desk",true).getId();
        assertEquals(db, cache);
    }

    @Test
    public void addNode() throws Exception {
        testNode = new MapNodeData("FakeNode", new Location(2080, 1280, Floor.THREE, "WPI"),
                NodeType.HALL, "FakeNode 001", "FN", "Team A", null);
        testCache.addNode(testNode);

        assertEquals(testNode.getShortDescription(), testCache.getNode(testNode.getId()).getShortDescription());
        assertEquals(testNode.getId(), testCache.getNode(testNode.getLongDescription(), true).getId());
        assertEquals(testNode.getId(), testCache.getNode(testNode.getShortDescription(), false).getId());
        assertTrue(testCache.getNodeIds().contains(testNode.getId()));
        assertTrue(testCache.getNodesOnFloor(testNode.getCoordinate().getLevel().toString()).contains(testNode));

        testNodeChange = new MapNodeData("FakeNode", new Location(2080, 1280, Floor.TWO, "WPI"),
                NodeType.HALL, "FakeNode 002", "FN2", "Team A", null);

        testCache.addNode(testNodeChange);
        assertEquals(testNodeChange.getShortDescription(), testCache.getNode(testNodeChange.getId()).getShortDescription());
        assertEquals(testNodeChange.getId(), testCache.getNode(testNodeChange.getLongDescription(), true).getId());
        assertEquals(testNodeChange.getId(), testCache.getNode(testNodeChange.getShortDescription(), false).getId());
        assertTrue(testCache.getNodeIds().contains(testNodeChange.getId()));
        assertTrue(testCache.getNodesOnFloor(testNodeChange.getCoordinate().getLevel().toString()).contains(testNodeChange));
    }

    @Test
    public void removeNode() throws Exception {
        MapNode testNode = testCache.getNode("FINFO00101");

        testCache.removeNode(testNode.getId());
        assertNull("not removed for get by id", testCache.getNode(testNode.getId()));
        assertNull("not removed for get by long describ ", testCache.getNode(testNode.getLongDescription(), true));
        assertNull("not removed for get by short describ ", testCache.getNode(testNode.getShortDescription(), false));
        assertFalse(testCache.getNodeIds().contains(testNode.getId()));
        assertFalse(testCache.getNodesOnFloor(testNode.getCoordinate().getLevel().toString()).contains(testNode));

    }

    @Test
    public void addEdge() throws Exception {
        n1 = (MapNodeData) testCache.getNode("FINFO00101");
        n2 = (MapNodeData) testCache.getNode("GSTAI01301");
        testEdge = new MapEdgeData("TESTEDGE", n1,n2);
        testCache.addEdge(testEdge);

        assertEquals(testEdge.getStartID(), testCache.getEdge(testEdge.getId()).getStartID());
        assertTrue(testCache.getEdgeIds().contains(testEdge.getId()));
    }

    @Test
    public void removeEdge() throws Exception {
        //remove Edge"GEXIT00101_GSTAI01301"
        testCache.removeEdge("GEXIT00101_GSTAI01301");
        assertFalse(testCache.getEdgeIds().contains("GEXIT00101_GSTAI01301"));
    }



    @Test
    public void getNodeIds() throws Exception {
        //check Emergence Entrance
        assertTrue(testCache.getNodeIds().contains("FEXIT00301"));
        //check Cafe Stairs
        assertTrue(testCache.getNodeIds().contains("ASTAI00101"));
        //check fake node
        assertFalse(testCache.getNodeIds().contains("FAKE_NODE"));

    }

    @Test
    public void getEdgeIds() throws Exception {
        //check edges on Cafe Stairs
        assertTrue(testCache.getEdgeIds().contains("AHALL00201_ASTAI00101"));
        assertTrue(testCache.getEdgeIds().contains("ASTAI00101_ASTAI00102"));
        //check fake edge
        assertFalse(testCache.getEdgeIds().contains("FAKE_EDGE"));
    }

    @Test
    public void getEdge() throws Exception {

        String testingId = javaDBSource.getEdgeIds().get((int)Math.random()*javaDBSource.getEdgeIds().size());
        // get a random edge to test
        String db = javaDBSource.getEdge(testingId).getId();
        String cache = testCache.getEdge(testingId).getId();
        assertEquals(db, cache);
        assertNull(testCache.getEdge("FakeEdge"));
    }

    @Test
    public void getNodesOnFloor() throws Exception {
        //check Emergence Entrance
        assertTrue(testCache.getNodesOnFloor("1").contains(testCache.getNode("FEXIT00301")));
        //check Cafe Stairs
        assertTrue(testCache.getNodesOnFloor("1").contains(testCache.getNode("ASTAI00101")));
        //check fake node
        assertFalse(testCache.getNodesOnFloor("1").contains(testCache.getNode("FAKE_NODE")));

    }

    @Test
    public void resetTest (){
        javaDBSource.addNode(testNode = new MapNodeData("FakeNode", new Location(2080, 1280, Floor.THREE, "WPI"),
                NodeType.HALL, "FakeNode 001", "FN", "Team A", null)
       );
        ( (TotalMapCache) testCache ).reset(javaDBSource);
        assertEquals("FakeNode",testCache.getNode(testNode.getId()).getId());
    }

}