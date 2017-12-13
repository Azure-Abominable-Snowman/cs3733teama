package com.teama.mapsubsystem.pathfinding.PathRelated;

import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.reverseAStar.ReverseAstar;
import com.teama.mapsubsystem.pathfinding.LongPathFinder.LongPathFinder;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PathAlgorithmTest {
    MapNode[][] map1 = new MapNode[20][20];
    MapNode[][] mapd = new MapNode[30][30];
    MapNode[][] map2 = new MapNode[100][100];
    MapNode[][] map3 = new MapNode[100][100];
    PathAlgorithm finder;

    @Before
    public void setUp() throws Exception {
        GenerateMap g = new GenerateMap();
        map1=g.GenerateNewMap(1);
        mapd=g.GenerateNewMap('d');
        map2=g.GenerateNewMap(2);
        map3=g.GenerateNewMap(3);
        g.printMap(map1,20,20);
        System.out.println();
        System.out.println();
        System.out.println();
        g.printMap(mapd,25,25);
        System.out.println();
        System.out.println();
        System.out.println();
        g.printMap(map2,100,100);
        g.printMap(map3,100,100);
    }

    @Test
    public void AStarGeneratePath() throws Exception {
        finder = new AStar();
        Path result =  finder.generatePath(map1[0][0],map1[12][1]);
        assertTrue(result.getNodes().contains(map1[0][0])); // check the start
        assertTrue(result.getNodes().contains(map1[12][1]));// check the end
        assertTrue(result.getNodes().contains(map1[0][4])); // check some points in the path.
        assertTrue(result.getNodes().contains(map1[11][4]));

        Path result2 = finder.generatePath(mapd[0][0],mapd[22][20]);
        result2 = finder.generatePath(mapd[0][0],mapd[13][14]);
        // give a impossible route. should throw the RuntimeException.
        boolean thrown = false;
        try {
            result2 = finder.generatePath(mapd[0][0],mapd[6][12]);
        }catch (RuntimeException e){
            thrown=true;
        }
        assertTrue(thrown);
    }

    @Test
    /**
     * Special test which AStar will be given a fake node with right name for the end.
     * to test if AStar use .equal or == for pointers.
     */
    public void AStarPointerEqualTest()
    {
        finder = new AStar();
        MapNode specialNode = new MapNodeData("C 12 R 1",new Location(12,1, Floor.ONE,"a"),
                NodeType.HALL,"C 12 R 1","C 12 R 1","A");
        Path result =  finder.generatePath(map1[0][0],specialNode);
        assertTrue(result.getNodes().contains(map1[0][0])); // check the start
        assertTrue(result.getNodes().contains(map1[12][1]));// check the end
        assertTrue(result.getNodes().contains(map1[0][4])); // check some points in the path.
        assertTrue(result.getNodes().contains(map1[11][4]));
    }

    @Test
    public void BFSGeneratePath() throws Exception{
        finder = new BreathFirst();
        //Path result =  finder.generatePath(map1[0][0], map1[12][1]);
        Path result3 = finder.generatePath(map2[0][0], map2[99][99]);
        assertTrue(result3.getNodes().contains(map2[0][0]));
        assertTrue(result3.getNodes().contains(map2[99][99]));

      /*  assertTrue(result.getNodes().contains(map1[0][0]));    //check the start
        assertTrue(result.getNodes().contains(map1[12][1]));   //check the end
        assertTrue(result.getNodes().contains(map1[1][1]));    //check some points in the path
        assertTrue(result.getNodes().contains(map1[6][1]));
        assertTrue(result.getNodes().contains(map1[11][1]));
        */

        Path result2 = finder.generatePath(mapd[0][0],mapd[22][20]);
        result2 = finder.generatePath(mapd[0][0],mapd[13][14]);
        // give a impossible route. should throw the RuntimeException.
        boolean thrown = false;
        try {
            result2 = finder.generatePath(mapd[0][0],mapd[6][12]);
        }catch (RuntimeException e){
            thrown=true;
        }
        assertTrue(thrown);
    }

    @Test
    public void BeamSearchTest() throws Exception {
        int queueSize = 7; // size too small cause got itself into dead end.
         finder = new BeamSearch(queueSize);
        Path result =  finder.generatePath(map1[0][0],map1[12][1]);
        assertTrue(result.getNodes().contains(map1[0][0])); // check the start
        assertTrue(result.getNodes().contains(map1[12][1]));// check the end
        assertTrue(result.getNodes().contains(map1[0][4])); // check some points in the path.
        assertTrue(result.getNodes().contains(map1[11][1]));
        // if this one passed, means the queue size is big enough for this to be the same as A* in this map.
        assertTrue(result.getNodes().contains(map1[11][4]));

        Path result2 = finder.generatePath(mapd[0][0],mapd[22][20]);
        result2 = finder.generatePath(mapd[0][0],mapd[13][14]);
        // give a impossible route. should throw the RuntimeException.
        boolean thrown = false;
        try {
            result2 = finder.generatePath(mapd[0][0],mapd[6][12]);
        }catch (RuntimeException e){
            thrown=true;
        }
        assertTrue(thrown);
    }

    @Test
    public void disableNodeTest() throws Exception{
        ArrayList<Integer> disIndex = new ArrayList<Integer>();
        disIndex.add(2);
        disIndex.add(3);
        //test Astar
        finder = new AStar();
        System.out.println("Testing AStart disable node.");
        disableNodeTestHelper(finder,map1[0][0],map1[4][4],disIndex);
        //test for Dijkstars
        finder = new Dijkstras();
        System.out.println("Testing Dinkstars disable node.");
        disableNodeTestHelper(finder, map1[0][0],map1[4][4],disIndex);
        //test for Beam Search
        int queueSize = 7;
        finder = new BeamSearch(queueSize);
        System.out.println("Testing Beam Search disable node.");
        disableNodeTestHelper(finder, map1[0][0],map1[4][4],disIndex);
        //test for BreathFirstSearch
        finder = new BreathFirst();
        System.out.println("Testing Breath First Search disable node.");
        disableNodeTestHelper(finder, map1[0][0],map1[4][4],disIndex);
    }

    private void disableNodeTestHelper (PathAlgorithm finder, MapNode start, MapNode end, ArrayList<Integer> disIndex)
    {
        // generate the result from normal mode
        ArrayList<MapNode> normalResult = finder.generatePath(start,end).getNodes();
        // grab a few nodes that are gonna be disabled.
        ArrayList<MapNode> disableNodes = new ArrayList<>();
        for (Integer index : disIndex) {
            disableNodes.add(normalResult.get(normalResult.size()-index));
        }

        ArrayList<MapNode> disableResult = finder.generatePath(start,end,disableNodes).getNodes();

        for (Integer index : disIndex) {
            System.out.printf("Testing new Path at it's Node %s  \n",normalResult.get(index).getId());
            assertNotEquals(disableResult.get(index).getId(), normalResult.get(index).getId());
        }
    }

    @Test
    public void reverseAStarTest()
    {
        finder = new ReverseAstar();
        ArrayList<MapNode> resultList = finder.generatePath(map3[0][0],map3[99][99]).getNodes();
        for (MapNode mapNode : resultList) {
            System.out.println(mapNode.getLongDescription());
        }
        System.out.println(resultList.size());

    }

    //@Test  // don't worry about this test as we won't use longest Path. 
    public void LongPathFinderTest()
    {
        ArrayList<MapNode> result;
        finder = new LongPathFinder();
         result = finder.generatePath(map1[0][0],map1[12][1]).getNodes();
        assertEquals(result.get(result.size()-1).getId(),map1[12][1].getId());
        System.out.printf("The LongestPath result is %d in length\n",result.size());
        // the mempry hogger.
   //     result = finder.generatePath(map2[0][0],map2[22][20]).getNodes();
      //  assertEquals(result.get(result.size()-1).getId(),map2[22][20].getId());
      //  System.out.printf("The LongestPath result is %d in length\n",result.size());

    }
}