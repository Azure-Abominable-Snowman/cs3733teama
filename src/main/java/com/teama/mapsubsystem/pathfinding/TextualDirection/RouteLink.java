package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;

public class RouteLink {

    private MapNode thisNode, nextNode;
    private double link1;


   private RouteLink(MapNode start, MapNode end){

       link1 = Math.toDegrees(Math.atan2(start.getCoordinate().getyCoord() - end.getCoordinate().getyCoord(),
               start.getCoordinate().getxCoord() - end.getCoordinate().getxCoord()));

   }

}
