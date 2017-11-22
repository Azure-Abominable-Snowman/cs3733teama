package com.teama.mapsubsystem.pathfinding;

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
}
