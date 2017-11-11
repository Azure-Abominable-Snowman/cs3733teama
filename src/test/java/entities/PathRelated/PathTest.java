package entities.PathRelated;

import com.sun.xml.internal.bind.v2.TODO;
import entities.Location;
import entities.MapNode;
import entities.NodeType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PathTest {
    // Testing objects
    MapNode[][] testNodes = new MapNode[3][3];
    Path testPath = new Path();


    @Before

    public void createMap(){

        // setup temp variable for generating 3x3 nodes
        String str  = new String();
        Location tempLoc = new Location(1,1,"1","a");
        for(int i = 0;i< 3;i++){
            for (int j = 0; j < 3; j++)
            {
                tempLoc.setxCoord(i);
                tempLoc.setyCoord(j);
                str=str.format("C%dR%d",i,j);
                testNodes[i][j]= new MapNode(str,tempLoc,NodeType.HALL,str
                        ,str,"A");
            }
        }

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