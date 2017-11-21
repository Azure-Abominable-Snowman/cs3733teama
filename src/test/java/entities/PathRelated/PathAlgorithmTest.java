package entities.PathRelated;

import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PathAlgorithmTest {
    MapNode[][] map1 = new MapNode[20][20];
    MapNode[][] mapd = new MapNode[30][30];
    PathAlgorithm finder;

    @Before
    public void setUp() throws Exception {
        GenerateMap g = new GenerateMap();
        map1=g.GenerateNewMap(1);
        mapd=g.GenerateNewMap('d');
        g.printMap(map1,20,20);
        System.out.println();
        System.out.println();
        System.out.println();
        g.printMap(mapd,25,25);
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
        Path result =  finder.generatePath(map1[0][0], map1[12][1]);
        assertTrue(result.getNodes().contains(map1[0][0]));    //check the start
        assertTrue(result.getNodes().contains(map1[12][1]));   //check the end
        assertTrue(result.getNodes().contains(map1[1][1]));    //check some points in the path
        assertTrue(result.getNodes().contains(map1[6][1]));
        assertTrue(result.getNodes().contains(map1[11][1]));

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

}