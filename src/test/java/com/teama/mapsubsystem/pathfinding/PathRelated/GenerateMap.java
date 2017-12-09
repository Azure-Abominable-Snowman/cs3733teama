package com.teama.mapsubsystem.pathfinding.PathRelated;

import com.teama.mapsubsystem.data.*;

public class  GenerateMap  {

    /**
     * This function is to generate different map baesd on version number.
     * Default map is 30*30 map.
     * Version1 is 20*20 map.
     * @param version
     * @return This function returns the MapNodeData.
     */
     public MapNode[][] GenerateNewMap(int version)
    {
        switch (version)
        {
            case 1: return GenerateV1();    // a 20*20 map
            case 2 : return GenerateV2();   // a 100*100 map
            case 3: return GenerateV3(); // a 100*100 map with weight of 1.
            default: return GenerateVdefult(); // a 30*30 map
        }
    }

    /**
     * This function is to generate version1 map (20*20).
     * @return 20*20 MapNode array
     */
    public MapNode[][] GenerateV1()  // Final version, do not change, make new map.
    {
        int sizeX=20, sizeY=20;
        if(sizeX < 5 ) sizeX=5;
        if(sizeY< 5 ) sizeY =5; // make sure no sizes are smaller then 5;

        MapNodeData[][] map = new MapNodeData[sizeX][sizeY]; // allocate new map
        fillMap (map,sizeX,sizeY);
        // Done creating the Nodes, Start making edges.
        linkNodes(map[0][0], map[0][1],1);
        linkNodes(map[0][1], map[0][2],2);
        linkNodes(map[0][4], map[0][5],3);
        linkNodes(map[0][0], map[1][0],2);
        linkNodes(map[1][0], map[1][1],1);
        linkNodes(map[1][1], map[2][1],15);
        linkNodes(map[2][1], map[3][1],14);
        linkNodes(map[0][5], map[1][5],8);
        linkNodes(map[1][5], map[2][5],8);
        linkNodes(map[3][1], map[3][2],12);

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

    /**
     * This function is to generate version2 map(100*100).
     * @return 100*100 MapNode array
     */
    public MapNode[][] GenerateV2()
    {
        int sizeX=100, sizeY=100;
        if(sizeX < 5 ) sizeX=5;
        if(sizeY< 5 ) sizeY =5; // make sure no sizes are smaller then 5;

        MapNodeData[][] map = new MapNodeData[sizeX][sizeY]; // allocate new map
        fillMap (map,sizeX,sizeY);


        for(int col = 0 ;col<sizeX;col++)
            for(int i=1;i<sizeY;++i)
                linkNodes(map[col][i-1],map[col][i],1+(i%5)+col%3);

        for(int row = 0; row<sizeY; row++)
            for(int i=1;i<sizeX;++i)
                linkNodes(map[i-1][row],map[i][row],1+(i*4%3)+row%4);

        return map;
    }


    /**
     * This function is to generate version3 map(100*100), with all weight be 1
     * @return 100*100 MapNode array
     */
    public MapNode[][] GenerateV3()
    {
        int sizeX=100, sizeY=100;
        if(sizeX < 5 ) sizeX=5;
        if(sizeY< 5 ) sizeY =5; // make sure no sizes are smaller then 5;

        MapNodeData[][] map = new MapNodeData[sizeX][sizeY]; // allocate new map
        fillMap (map,sizeX,sizeY);


        for(int col = 0 ;col<sizeX;col++)
            for(int i=1;i<sizeY;++i)
                linkNodes(map[col][i-1],map[col][i],0);

        for(int row = 0; row<sizeY; row++)
            for(int i=1;i<sizeX;++i)
                linkNodes(map[i-1][row],map[i][row],0);
        return map;
    }


    /**
     * This function is to generate default map(30*30).
     * @return 30*30 MapNode array
     */
    public MapNode[][] GenerateVdefult()  // need change.
    {
        int sizeX=30, sizeY=30;
        if(sizeX <5 ) sizeX=5;
        if(sizeY<5 ) sizeY =5; // make sure no sizes are smaller then 5;

        MapNodeData[][] map = new MapNodeData[sizeX][sizeY]; // allocate new map
        fillMap (map,sizeX,sizeY);
        // Done creating the Nodes, Start making edges.
        // add individual edges.
        linkNodes(map[0][0], map[0][1],1);
        linkNodes(map[10][1], map[11][1],2);
        linkNodes(map[12][1], map[11][1],1);

        linkNodes(map[23][19], map[24][19],3);
        linkNodes(map[23][17], map[24][17],3);
        linkNodes(map[23][22], map[24][22],3);


        linkNodes(map[22][22], map[22][23],3);
        linkNodes(map[23][22], map[23][23],3);
        linkNodes(map[22][23], map[22][24],3);
        linkNodes(map[23][23], map[23][24],3);

        linkNodes(map[20][23], map[20][24],3);
        linkNodes(map[20][22], map[20][23],3);



        // add vertical edges.
        for(int i=2 ,col=10;i<9;++i) // create the map for part 10th col
            linkNodes(map[col][i-1],map[col][i],1+i*3%2);
        for(int i=2 ,col=12;i<11;++i) // create the map for part 5th col
        linkNodes(map[col][i-1],map[col][i],1+i*4%3);

        for(int i=14 ,col=24;i<25;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1+i*4%3);
        for(int i=9,col=10;i<17;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],9+i*8%7);
        for(int i=1,col=2;i<9;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],5+i*8%7);


        // add horizontial edges.
        for (int i = 1,row=1; i < 11; ++i)
            linkNodes(map[i - 1][row], map[i][row], 3+i*8%7);
        for (int i = 11,row=8; i < 13; ++i)
            linkNodes(map[i - 1][row], map[i][row], 12+i*3%4);
        for (int i = 21,row=23; i < 25; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*2%3);
        for (int i = 21,row=24; i < 25; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);



        for (int i = 22,row=15; i < 25; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for(int i=15 ,col=21;i<20;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1+i*4%3);

        for (int i =19,row=17; i < 23; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for (int i =17,row=18; i < 23; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for (int i =19,row=13; i < 25; ++i)
            linkNodes(map[i - 1][row], map[i][row], 10+i*3%6);
        for(int i=7 ,col=18;i<20;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1+i*4%3);
        for(int i=3 ,col=16;i<20;++i) // create the map for part 5th col
            linkNodes(map[col][i-1],map[col][i],1+i*7%4);
        for (int i =11,row=2; i < 17; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for (int i =11,row=14; i < 19; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for (int i =3,row=8; i < 11; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*7%3);

        for (int i =11,row=17; i < 19; ++i)
        linkNodes(map[i - 1][row], map[i][row], 8+i*5%4);
        for (int i =3,row=4; i < 11; ++i)
            linkNodes(map[i - 1][row], map[i][row], 1+i*3%2);
        for (int i =12,row=6; i <19; ++i)
            linkNodes(map[i - 1][row], map[i][row], 9+i*5%4);



        for (int row = 19;row<23 ;++row )
            for (int i = 17; i < 24; ++i)
                linkNodes(map[i - 1][row], map[i][row], 15);
        for (int col =16; col<24;++col)
            for(int i=20 ;i<23;++i) // create the map for part 5th col
                linkNodes(map[col][i-1],map[col][i],15);


        for (int row = 16;row<19 ;++row )
                linkNodes(map[22][row], map[23][row], 15);

        for (int col =22; col<24;++col)
            for(int i=16 ;i<20;++i) // create the map for part 5th col
                linkNodes(map[col][i-1],map[col][i],15);




        //make island
        for (int row = 10;row<15 ;++row )
            for (int i = 4; i < 9; ++i)
                linkNodes(map[i - 1][row], map[i][row], 2+row%3 + ((i / 2) % 3));
        for (int col =3; col<9;++col)
            for(int i=11 ;i<15;++i) // create the map for part 5th col
                linkNodes(map[col][i-1],map[col][i],3+(i%10));


        return map;
    }


    ///////////helper////////////

    /**
     * This function is to fill Map up with simple node at type HallWay.
     * @param map   is the map to be filled.
     * @param sizeX is the width of map (can be smaller, but no bigger then actual map)
     * @param sizeY is the height of map (can be smaller, but no bigger then actual map)
     * @return This function returns void.
     */
    private void fillMap (MapNodeData[][] map, int sizeX, int sizeY)
    {
        String str  = new String();
        for(int i = 0;i< sizeX;i++){    // start fill in the map
            for (int j = 0; j < sizeY; j++)
            {
                Location tempLoc = new Location(i,j, Floor.ONE,"a");// create location
                str=str.format("C %d R %d",i,j);    // create correct name
                map[i][j]= new MapNodeData(str,tempLoc, NodeType.HALL,str
                        ,str,"A");  // create the Node in the map
            }
        }
    }

    /**
     * This is a helper function to create the edge between two nodes and add the edge to two nodes.
     * @param node1 is the first node.
     * @param node2 is the second node.
     * @param weight is the weight between two nodes.
     * @return This function returns void.
     */
    public void linkNodes(MapNodeData node1, MapNodeData node2, double weight){
        String temp = String.format("%s - %s",node1.getId(),node2.getId());
        MapEdgeData edge = new MapEdgeData(temp, node1, node2, weight);
         node1.addEdge(edge);
         node2.addEdge(edge);
    }

    /**
     * This is a helper function that print the maps generated in this class onto commandline.
     * @param map   is the map to be printed.
     * @param sizeX is the width of map (can be smaller, but no bigger then actual map)
     * @param sizeY is the height of map (can be smaller, but no bigger then actual map)
     */
    public void printMap (MapNode[][] map, int sizeX, int sizeY)
    {
        System.out.printf("    ");
        for(int i=0;i<sizeX;i++)
        {
            System.out.printf("%d%d    ",i/10,i%10);
        }
        System.out.println();
        for(int j=0;j<sizeY-1;j++) // Start print the map.
        {
            System.out.printf("%d%d  ",j/10,j%10);
            for(int i=0;i<sizeX-1;i++)
            {
                System.out.print("X  ");
                if(findWeight(map[i][j],map[i+1][j])<0) System.out.print("   ");
                else System.out.printf("%h  ",(int) findWeight(map[i][j],map[i+1][j]));
            }
            System.out.print("X\n");
            System.out.printf("    ");
            for(int i=0;i<sizeX;i++)
            {
                if(findWeight(map[i][j],map[i][j+1])<0) System.out.print("      ");
                else System.out.printf("%h     ",(int) findWeight(map[i][j],map[i][j+1]));
            }
            System.out.print("\n");
        }

        System.out.printf("%d%d  ",(sizeY-1)/10,(sizeY-1)%10);
        for(int i=0;i<sizeX-1;i++)
        {
            System.out.print("X  ");
            if(findWeight(map[i][sizeY-1],map[i+1][sizeY-1])<0) System.out.print("   ");
            else System.out.printf("%h  ",(int) findWeight(map[i][sizeY-1],map[i+1][sizeY-1]));
        }
        System.out.print("X\n");
    }

    /**
     *This is a helper function to find the weight of the edge between given two nodes.
     * @param n1 is the node 1
     * @param n2 is the node 2
     * @return This function returns the weight of the edge connecting node 1 and node 2, return -1 when no edge.
     */
    private double findWeight(MapNode n1, MapNode n2)
    {
        for (MapEdge edge: n1.getEdges()) {
            if ( edge.getEnd()==n2 || edge.getStart() == n2 ) return edge.getWeight();
        }
        return -1;
    }
}
