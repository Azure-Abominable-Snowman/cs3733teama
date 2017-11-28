package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;

import java.awt.*;

public class AngleGenerator {

    //TODO Make a Node to Point Conversion
    Point from; // Point where the Node/ Point is measuring from


    public float AngleGenerator(Point target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - from.y, target.x - from.x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public static float positionAngle(MapNode start, MapNode end) {
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
        float angle = AngleGenerator.positionAngle(start,thisNode) - AngleGenerator.positionAngle(thisNode,end);
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }



}
