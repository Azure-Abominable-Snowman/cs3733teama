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

        // todo make the start and end direction
        RouteLink thisTurn = routeLinks.get(0);
        RouteLink nextLink;
        int i=0;
        nextLink = routeLinks.get(i);

        for(;i<routeLinks.size();routeLinks.get(i++)) {
            // in the case of two stair links next to each other combing all the stair situation
            if( (thisTurn.getTextReturn().contains("Elevator") || thisTurn.getTextReturn().contains("Stairs"))){
                if((nextLink.getTextReturn().contains("Elevator")|| nextLink.getTextReturn().contains("Stairs"))){
                    combineFloorChange(thisTurn,nextLink); // combine the next one into this.
                    continue;
                }
            }

            // case of the link right out of the elevator, ignore this for now.
            if( thisTurn.getTextReturn().contains("No") )
            {
                thisTurn= nextLink;
                continue;
            }

            if (nextLink.getTextReturn().contains("Straight")) {
                addDistance(thisTurn, nextLink); // combine the next one into this.
                continue;
            }

            formDirection(thisTurn);
            thisTurn=nextLink;
        }

    return null;
    }

    private static RouteLink addDistance(RouteLink turnLink, RouteLink straightLink)
    {
        turnLink.setDistance(turnLink.getDistance()+ straightLink.getDistance());
        return turnLink;
    }

    private Direction formDirection (RouteLink routeLink)
    {
        String discription = routeLink.getTextReturn();
        if (discription.contains("Elevator")){ // elevator text
            discription = String.format("Enter Elevator and exit on %s",
                    routeLink.getNextFloor().toString());
        }
        else if(discription.contains("Stairs")){ // Stairs text
            discription = String.format("Enter Stair and exit on %s",
                    routeLink.getNextFloor().toString());
        }
        else if(discription.contains("Straight")){// going Straight
            discription = String.format("Walk Straight for %f distance",
                    routeLink.getDistance());
        }
        else{ // actually turning.
            discription=String.format("%s and walk for %f distance",
                    discription,routeLink.getDistance());
        }

        // create and return the new formed Direction object.
        return new Direction(0,
                routeLink.getStart().getCoordinate(),
                routeLink.getNext().getCoordinate(),
                discription);
    }

    private RouteLink combineFloorChange (RouteLink baseLink, RouteLink nextLink)
    {
        baseLink.setNextFloor(nextLink.getNextFloor());
        return baseLink;
    }

}
