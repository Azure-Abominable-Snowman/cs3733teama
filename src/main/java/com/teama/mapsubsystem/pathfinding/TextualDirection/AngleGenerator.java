package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;

import java.awt.*;

public class AngleGenerator {


    public static float vectorAngle (MapNode start, MapNode end) {
        float angle = (float) Math.toDegrees(Math.atan2(end.getCoordinate().getyCoord() - start.getCoordinate().getyCoord(),
                end.getCoordinate().getxCoord() - start.getCoordinate().getxCoord()));

        for(;angle>=180;angle-=360); // keep the angles below 180
        for (;angle<-180;angle+=360);   // keep the angles above -180


        return angle;
    }

    // todo shouldn't be needed.
    public static float turnAngle(MapNode start, MapNode thisNode, MapNode end) {
        float angle = AngleGenerator.vectorAngle(start,thisNode) - AngleGenerator.vectorAngle(thisNode,end);
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

}
