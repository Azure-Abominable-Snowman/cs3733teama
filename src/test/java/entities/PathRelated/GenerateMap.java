package entities.PathRelated;

import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import entities.NodeType;

public class GenerateMap {




     public MapNode[][] GenerateNewMap(int sizeX, int sizeY, int version)
    {
        switch (version)
        {
            case 1: return GenerateV1(sizeX,sizeY);
            default: return GenerateVdefult(sizeX,sizeY);
        }


    }

    public MapNode[][] GenerateV1(int sizeX,int sizeY) // assume bigger then 20*20
    {

        MapNode[][] map = new MapNode[sizeX][sizeY]; // allocate new map

        String str  = new String();
        Location tempLoc = new Location(1,1,"1","a");// create location

        for(int i = 0;i< sizeX;i++){    // start fill in the map
            for (int j = 0; j < sizeY; j++)
            {
                tempLoc.setxCoord(i);   // put in correct coord
                tempLoc.setyCoord(j);
                str=str.format("C %d R %d",i,j);    // create correct name
                map[i][j]= new MapNode(str,tempLoc, NodeType.HALL,str
                        ,str,"A");  // create the Node in the map
            }
        }

        // Done creating the Nodes, Start making edges.
        linkNodes(map[0][0], map[0][1],1);
        linkNodes(map[0][2], map[0][3],2);
        linkNodes(map[0][4], map[0][5],3);


        return map;
    }

    public MapNode[][] GenerateVdefult(int sizeX,int sizeY)
    {
        return null;
    }


    //////helper

    /**
     * This is the helper function to create the edge between two nodes and add the edge to two nodes.
     * @param node1 is the first node.
     * @param node2 is the second node.
     * @param weight is the weight between two nodes.
     * @return This function returns void.
     */
    public void linkNodes(MapNode node1, MapNode node2, double weight){
        MapEdge edge = new MapEdge("e1", node1, node2, weight);
         node1.addEdge(edge);
         node2.addEdge(edge);
    }






}
