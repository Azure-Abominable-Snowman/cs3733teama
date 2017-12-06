package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.*;

import java.util.ArrayList;
import java.util.Arrays;

public class GenerateTextTestMap {

    ArrayList<MapNodeData> map1 ;
    ArrayList<MapNodeData> map2 ;
    ArrayList<MapNodeData> map3 ;
    ArrayList<MapNodeData> fullMap;

    GenerateTextTestMap()
    {
        map1 = new ArrayList<>();
        map2 = new ArrayList<>();
        map3 = new ArrayList<>();

        // put in map1
        String mapcords = "10 80 12 87 20 88 30 86 33 77 40 77 45 77 53 77 ";
        mapcords+= "60 77 57 70 56 63 49 66 44 62 ";
        mapcords+= "35 58 28 59 20 58";
        map1=createMap(mapcords,Floor.BASEMENT);
        map1.add(createNode(15,57,Floor.BASEMENT,NodeType.ELEV));
        // fake node for jumping around elevator node.
        MapNodeData endOfMap1 = createNode(15,56,Floor.THREE,NodeType.ELEV);
        map1.add(endOfMap1);

        MapNodeData startOfMap2 = (createNode(1,49,Floor.GROUND,NodeType.ELEV));
        map2.add(startOfMap2);
        mapcords = "8 49 14 49 20 49  23 48  26 49  29 50  31 49  28 40  25 35 ";
        mapcords+= " 20 30  32 29 38 33 ";
        map2.addAll(createMap(mapcords,Floor.GROUND));
        map2.add(createNode(49,37,Floor.GROUND,NodeType.STAI));
        // fake node for jumping around stair nodes.
        map2.add(createNode(49,38,Floor.TWO,NodeType.STAI));
        map2.add(createNode(49,38,Floor.BASEMENT,NodeType.STAI));
        MapNodeData endOfMap2 = createNode(38,49,Floor.ONE,NodeType.STAI);
        map2.add(endOfMap2);

        MapNodeData startOfMap3 = (createNode(1,21,Floor.ONE,NodeType.STAI));
        map3.add(startOfMap3);
        mapcords = "5 21  4 15  6 11  4 7  13 8  20 15  26 15";
        map3.addAll(createMap(mapcords,Floor.ONE));
        linkMap(map1);
        linkMap(map2);
        linkMap(map3);
        linkNodes(endOfMap1,startOfMap2,0);
        linkNodes(endOfMap2,startOfMap3,0);

        fullMap=map1;
        fullMap.addAll(map2);
        fullMap.addAll(map3);
    }


    public void linkMap(ArrayList<MapNodeData> list )
    {
        for(int i=1 ; i<list.size();++i)
        {
            linkNodes(list.get(i-1),list.get(i),1);
        }
    }

    public ArrayList<MapNodeData> createMap(String cords , Floor floor)
    {
        ArrayList<MapNodeData> map = new ArrayList<>() ;
        String[] temp = cords.split("\\s+");
        ArrayList<String> cordList = new ArrayList<String>(Arrays.asList(temp));
        String str;
        for(int i=0;i<cordList.size();++i)
        {

            int x = Integer.parseInt(cordList.get(i++));
            int y = Integer.parseInt(cordList.get(i));
            map.add(createNode(x,y,floor,NodeType.HALL));
        }
        return map;
    }

    public MapNodeData createNode (int x, int y, Floor floor, NodeType type)
    {
        Location tempLoc = new Location(x,y,floor,"a");// create location
        String str=String.format("X:%d Y:%d",x,y);    // create correct name
        return new MapNodeData(str,tempLoc, type , str
                ,str,"A");
    }


    public void linkNodes(MapNodeData node1, MapNodeData node2, double weight){
        String temp = String.format("%s - %s",node1.getId(),node2.getId());
        MapEdgeData edge = new MapEdgeData(temp, node1, node2, weight);
        node1.addEdge(edge);
        node2.addEdge(edge);
    }
}
