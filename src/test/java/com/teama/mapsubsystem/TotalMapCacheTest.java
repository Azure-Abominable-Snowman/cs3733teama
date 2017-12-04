package com.teama.mapsubsystem;

import com.teama.Configuration;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.PathGenerator;
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

    @Before
    public void setUp() throws Exception {

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

        //TODO: Automatically detect to see if we need to populate the database with the CSV files
        csvSource = new CSVDatabaseSource(nList, eList, null, null);
        javaDBSource = new JavaDatabaseSource(dbURL, nodeTable, edgeTable);
        javaDBSource.addAll(csvSource);
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

        assertEquals(testNodeChange.getShortDescription(), testCache.getNode(testNodeChange.getId()).getShortDescription());
        assertEquals(testNodeChange.getId(), testCache.getNode(testNodeChange.getLongDescription(), true).getId());
        assertEquals(testNodeChange.getId(), testCache.getNode(testNodeChange.getShortDescription(), false).getId());
        assertTrue(testCache.getNodeIds().contains(testNodeChange.getId()));
        assertTrue(testCache.getNodesOnFloor(testNodeChange.getCoordinate().getLevel().toString()).contains(testNodeChange));
    }

    @Test
    public void removeNode() throws Exception {

        MapNode testNode = testCache.getNode("FINFO00101"); // grab it from somewhere

        testCache.removeNode(testNode.getId());
        assertNull(testNode.getShortDescription(), testCache.getNode(testNode.getId()).getShortDescription());
        assertNull(testNode.getId(), testCache.getNode(testNode.getLongDescription(), true).getId());
        assertNull(testNode.getId(), testCache.getNode(testNode.getShortDescription(), false).getId());
        assertFalse(testCache.getNodeIds().contains(testNode.getId()));
        assertFalse(testCache.getNodesOnFloor(testNode.getCoordinate().getLevel().toString()).contains(testNode));

    }

    @Test
    public void addEdge() throws Exception {
        n1 = (MapNodeData) testCache.getNode("FINFO00101");
        n2 = (MapNodeData) testCache.getNode("GSTAI01301");
        testEdge = new MapEdgeData("TESTEDGE", n1,n2);
        testCache.addEdge(testEdge);

        assertEquals(testEdge.getId(), testCache.getEdge(testEdge.getId()));
        
        assertEquals(testEdge.getId(), testCache.getNode(testNode.getLongDescription(), true).getId());
        assertEquals(testEdge.getId(), testCache.getNode(testNode.getShortDescription(), false).getId());
        assertTrue(testCache.getNodeIds().contains(testNode.getId()));
        assertTrue(testCache.getNodesOnFloor(testNode.getCoordinate().getLevel().toString()).contains(testNode));

    }

    @Test
    public void removeEdge() throws Exception {
        testCache.addEdge(testEdge);
        testCache.removeEdge("TestEdge");
        assertEquals(false, testCache.getEdge("TestEdge"));

    }

    @Test
    public void getNodeIds() throws Exception {


    }

    @Test
    public void getEdgeIds() throws Exception {
    }

    @Test
    public void getEdge() throws Exception {
        String db = javaDBSource.getEdge("FHALL02901_FINFO001001").getId();
        String cache = testCache.getEdge("FHALL02901_FINFO001001").getId();
        assertEquals(db, cache);
    }

    @Test
    public void close() throws Exception {
    }

    @Test
    public void getNodesOnFloor() throws Exception {
    }

    @Test
    public void getEdgesOnFloor() throws Exception {
    }

}