package entities.PathRelated;

import entities.MapNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AStarTest {
    MapNode[][] map1 = new MapNode[20][20];

    @Before
    public void setUp() throws Exception {
        GenerateMap g = new GenerateMap();
        map1=g.GenerateNewMap(1);
        g.printMap(map1,20,20);
    }

    @Test
    public void generatePath() throws Exception {
        AStar finder = new AStar();
        Path result =  finder.generatePath(map1[0][0],map1[12][1]);
        result.getNodes();
    }

}