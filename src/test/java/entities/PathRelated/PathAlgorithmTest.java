package entities.PathRelated;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Path;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PathAlgorithmTest {
    MapNode[][] map1 = new MapNode[20][20];
    MapNode[][] mapd = new MapNode[30][30];

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
        AStar finder = new AStar();
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
    public void BFSGeneratePath() throws Exception{
        BreathFirst finder = new BreathFirst();
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

}