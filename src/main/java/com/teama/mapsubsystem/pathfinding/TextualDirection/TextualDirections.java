package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;

import java.util.ArrayList;
import java.util.Map;

public class TextualDirections implements DirectionsGenerator {
    ArrayList<MapNode> nodeList;
    ArrayList<RouteLink> routeLinks ;
    ArrayList<Direction> dirList;

    TextualDirections()
    {
        nodeList = new ArrayList<>();
    }

    @Override
    public TextDirections generateDirections(Path path) {
        nodeList = path.getNodes();
         routeLinks = new ArrayList<>(nodeList.size()-1);

        // group list of nodes into two point link structure and let RouteLink do the basic calculation.
        RouteLink lastLink  = new RouteLink(nodeList.get(0),nodeList.get(1),null);
        routeLinks.add(lastLink);
        RouteLink thisLink ;
        for(int i=2;i<nodeList.size();++i)
        {
            thisLink = new RouteLink(nodeList.get(i-1),nodeList.get(i),lastLink);
            lastLink=thisLink;
            routeLinks.add(lastLink);
        }

         dirList = new ArrayList<>();

        // put RouteLinks into Direction formate and refactor the same RouteLinks.

        RouteLink thisTurn = routeLinks.get(0);
        RouteLink nextLink;
        int i=0;
        nextLink = routeLinks.get(i);
        for(;i<routeLinks.size();) {
            // combing all the stair situation
            if(nextLink.getTextReturn().contains("Elevator") || nextLink.getTextReturn().contains("Stairs"))
            {

            }
            if()
            {

            }

            if (nextLink.getTextReturn().contains("Straight")) {
                addDistance(thisTurn, nextLink);
                continue;
            }
            formDirection(thisTurn);
            thisTurn=nextLink;
        }



    return null;
    }

    private RouteLink addDistance(RouteLink turnLink, RouteLink straightLink)
    {
        return null;
    }

    private Direction formDirection (RouteLink routeLink)
    {
        return null;
    }


}
