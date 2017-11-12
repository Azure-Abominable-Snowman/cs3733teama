package entities.PathRelated;

import entities.Location;
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

    public MapNode[][] GenerateV1(int sizeX,int sizeY)
    {

        MapNode[][] map = new MapNode[sizeX][sizeY];

        String str  = new String();
        Location tempLoc = new Location(1,1,"1","a");
        for(int i = 0;i< sizeX;i++){
            for (int j = 0; j < sizeY; j++)
            {
                tempLoc.setxCoord(i);
                tempLoc.setyCoord(j);
                str=str.format("C %d R %d",i,j);
                map[i][j]= new MapNode(str,tempLoc, NodeType.HALL,str
                        ,str,"A");
            }
        }
        return map;
    }

    public MapNode[][] GenerateVdefult(int sizeX,int sizeY)
    {
        return null;
    }





}
