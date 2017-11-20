package entities.PathRelated;

import entities.MapNode;
import com.teama.mapsubsystem.pathfinding.AStar.KnownPoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnownPointTest {



    MapNode[][] map1 = new MapNode[20][20];
    MapNode[][] mapd = new MapNode[30][30];
    KnownPoint p00 ;
    KnownPoint p01 ;

    @Before
    public void setUp() throws Exception {
        GenerateMap generator = new GenerateMap();
        map1=generator.GenerateNewMap(1);
        mapd=generator.GenerateNewMap('d');

         p00 = new KnownPoint(map1[0][0],null,0
        ,20);
         p01 = new KnownPoint(map1[0][1],p00,1
                ,30);

    }

    @Test
    public void getEdge() throws Exception {
    }

    @Test
    public void compareTo() throws Exception {
        assertEquals(-1,p00.compareTo(p01));
    }

}