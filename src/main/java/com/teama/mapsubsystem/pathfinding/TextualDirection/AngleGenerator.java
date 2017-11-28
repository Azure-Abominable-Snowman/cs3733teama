package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;

import java.awt.*;

public class AngleGenerator {


    public static float vectorAngle (MapNode start, MapNode end) {
        float angle = (float) Math.toDegrees(Math.atan2(start.getCoordinate().getyCoord() - end.getCoordinate().getyCoord(),
                start.getCoordinate().getxCoord() - end.getCoordinate().getxCoord()));
       /*
        if(angle < 0){
            angle += 360;
        }
        */
        for(;angle>=180;angle-=360);
        for (;angle<-180;angle+=360);


        return angle;
    }

    public static float turnAngle(MapNode start, MapNode thisNode, MapNode end) {
        float angle = AngleGenerator.vectorAngle(start,thisNode) - AngleGenerator.vectorAngle(thisNode,end);
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

}
