package entities.PathRelated;

import entities.MapNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AStarTest {
    MapNode[][] map1 = new MapNode[20][20];
    MapNode[][] mapd = new MapNode[30][30];

    @Before
    public void setUp() throws Exception {
        GenerateMap g = new GenerateMap();
        map1=g.GenerateNewMap(1);
        mapd=g.GenerateNewMap('d');
        g.printMap(mapd,25,25);
    }

    @Test
    public void generatePath() throws Exception {
        AStar finder = new AStar();
        Path result =  finder.generatePath(map1[0][0],map1[12][1]);
        assertTrue(result.getNodes().contains(map1[0][0])); // check the start
        assertTrue(result.getNodes().contains(map1[12][1]));// check the end
        assertTrue(result.getNodes().contains(map1[0][4])); // check some points in the path.
        assertTrue(result.getNodes().contains(map1[11][4]));

        Path result2 = finder.generatePath(mapd[0][0],mapd[22][20]);


    }

}