package entities.PathRelated;

import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import entities.NodeType;

public class  GenerateMap  {




     public MapNode[][] GenerateNewMap(int version)
    {
        switch (version)
        {
            case 1: return GenerateV1();    // a 20*20 map
            default: return GenerateVdefult(); // a 30*30 map
        }


    }


    //Map1 at 20*20
    public MapNode[][] GenerateV1()  // Final version, do not change, make new map.
    {
        int sizeX=20, sizeY=20;
        if(sizeX <5 ) sizeX=5;
        if(sizeY<5 ) sizeY =5; // make sure no sizes are smaller then 5;

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
        linkNodes(map[0][1], map[0][2],2);
        linkNodes(map[0][4], map[0][5],3);
        linkNodes(map[0][0],map[1][0],2);
        linkNodes(map[1][0],map[1][1],1);
        linkNodes(map[1][1],map[2][1],15);
        linkNodes(map[2][1],map[3][1],14);
        linkNodes(map[0][5],map[1][5],8);
        linkNodes(map[1][5],map[2][5],8);
        linkNodes(map[3][1],map[3][2],12);

        for(int i=4 ,col=4;i<sizeX;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],3+(i%10));
        for(int i=3 ,col=0;i<5;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],0+((i/2)%10));
        for(int i=2 ,col=4;i<4;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1);
        for(int i=1 ,col=11;i<8;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],3+i%3*3%4);

        for(int i=1;i<sizeX;++i) // create the map for part 5th row
            linkNodes(map[i-1][4],map[i][4],2+((i/2)%3));
        for(int i=2,row=2;i<13;++i) // create the map for part 3th row
            linkNodes(map[i-1][row],map[i][row],6+((i/3)%3));
        for(int i=4,row=1;i<15;++i) // create the map for part 2th row
            linkNodes(map[i-1][row],map[i][row],1+((i*19)%7));

        return map;
    }

    //Map2 at 30*30
    public MapNode[][] GenerateVdefult()  // need change.
    {
        int sizeX=30, sizeY=30;
        if(sizeX <5 ) sizeX=5;
        if(sizeY<5 ) sizeY =5; // make sure no sizes are smaller then 5;

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
        linkNodes(map[0][1], map[0][2],2);
        linkNodes(map[0][4], map[0][5],3);
        linkNodes(map[0][0],map[1][0],2);
        linkNodes(map[1][0],map[1][1],1);
        linkNodes(map[2][1],map[3][1],14);
        linkNodes(map[0][5],map[1][5],8);
        linkNodes(map[1][5],map[2][5],8);
        linkNodes(map[3][1],map[3][2],12);

        for(int i=4 ,col=4;i<sizeX;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],3+(i%10));
        for(int i=3 ,col=0;i<5;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],0+((i/2)%10));
        for(int i=2 ,col=4;i<4;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1);
        for(int i=1 ,col=11;i<8;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],3+i%3*3%4);

        for(int i=1;i<sizeX;++i) // create the map for part 5th row
            linkNodes(map[i-1][4],map[i][4],2+((i/2)%3));
        for(int i=2,row=2;i<13;++i) // create the map for part 3th row
            linkNodes(map[i-1][row],map[i][row],6+((i/3)%3));
        for(int i=4,row=1;i<15;++i) // create the map for part 2th row
            linkNodes(map[i-1][row],map[i][row],1+((i*19)%7));

        return map;
    }



    ///////////helper////////////

    /**
     * This is the helper function to create the edge between two nodes and add the edge to two nodes.
     * @param node1 is the first node.
     * @param node2 is the second node.
     * @param weight is the weight between two nodes.
     * @return This function returns void.
     */
    public void linkNodes(MapNode node1, MapNode node2, double weight){
        String temp = String.format("%s - %s",node1.getId(),node2.getId());
        MapEdge edge = new MapEdge(temp, node1, node2, weight);
         node1.addEdge(edge);
         node2.addEdge(edge);
    }

    public void printMap (MapNode[][] map,int sizeX, int sizeY)
    {
        for(int i=0;i<sizeX;i++)
        {

        }
        for(int j=0;j<sizeY-1;j++)
        {
            for(int i=0;i<sizeX-1;i++)
            {
                System.out.print("X  ");
                if(findWeight(map[i][j],map[i+1][j])<0) System.out.print("   ");
                else System.out.printf("%h  ",(int) findWeight(map[i][j],map[i+1][j]));
            }
            System.out.print("X\n");
            for(int i=0;i<sizeX;i++)
            {
                if(findWeight(map[i][j],map[i][j+1])<0) System.out.print("      ");
                else System.out.printf("%h     ",(int) findWeight(map[i][j],map[i][j+1]));
            }
            System.out.print("\n");
        }
        for(int i=0;i<sizeX-1;i++)
        {
            System.out.print("X  ");
            if(findWeight(map[i][sizeY-1],map[i+1][sizeY-1])<0) System.out.print("   ");
            else System.out.printf("%h  ",(int) findWeight(map[i][sizeY-1],map[i+1][sizeY-1]));
        }
    }



    // helper
    private double findWeight(MapNode n1,MapNode n2)
    {
        for (MapEdge edge: n1.getEdges()) {
            if ( edge.getEnd()==n2 || edge.getStart() == n2 ) return edge.getWeight();
        }
        return -1;
    }
}
