package entities.PathRelated;

import entities.MapNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PathTest {
    // Testing objects
    MapNode[][] testNodes = new MapNode[20][20];
    Path testPath = new Path();


    @Before

    public void createMap(){
        GenerateMap generater = new GenerateMap();
        testNodes = new MapNode[20][20];
       testNodes=  generater.GenerateNewMap(1);

       generater.printMap(testNodes,20,20);
    }


    @Test
    public void addNode() throws Exception{

        //test if the node is added successfully
        testPath.addNode(testNodes[2][1]);
        assertEquals(testNodes[2][1], testPath.getNodes().get(0));

        //test whether the node is added or not
        assertEquals(true, testPath.addNode(testNodes[1][1]));
        testPath.addNode(testNodes[1][2]);
        assertEquals(false, testPath.addNode(testNodes[1][2]));
    }

    @Test
    public void addEdge() throws Exception {
    }

}