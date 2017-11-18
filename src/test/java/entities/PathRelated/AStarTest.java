package entities.PathRelated;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.AStar;
import com.teama.mapsubsystem.pathfinding.Path;
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
        assertTrue(result.getNodes().contains(map1[0][0])); // check the start
        assertTrue(result.getNodes().contains(map1[12][1]));// check the end
        assertTrue(result.getNodes().contains(map1[0][4])); // check some points in the path.
        assertTrue(result.getNodes().contains(map1[11][4]));
    }

}