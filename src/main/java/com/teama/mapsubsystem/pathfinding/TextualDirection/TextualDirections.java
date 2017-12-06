package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.translator.Translator;


import java.util.ArrayList;

public class TextualDirections implements DirectionsGenerator {
    private ArrayList<MapNode> nodeList;
    private ArrayList<RouteLink> routeLinks ;
    private ArrayList<Direction> dirList;

    public TextualDirections()
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
        // make the start Direction and push it into list.
        RouteLink thisTurn = routeLinks.get(0);
        String temp = String.format( "%s %s",
                Translator.getInstance().getText("pathstart"),
                thisTurn.getNext().getLongDescription());

        // make the start section.
        dirList.add(new Direction(thisTurn.getDistance(),
                thisTurn.getStart().getCoordinate()
                ,thisTurn.getNext().getCoordinate(),
                temp,thisTurn.getTurn()));

        thisTurn= routeLinks.get(1);
        RouteLink nextLink;
        for(int i=2;i<routeLinks.size();i++) {
            nextLink=routeLinks.get(i);
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
            // condenced turn and floor change case.
            dirList.add(formDirection(thisTurn));
            thisTurn=nextLink;
        }
        // create the end link.
        thisTurn.setEndFlag(true);
        dirList.add(formDirection(thisTurn));

        //warp the list into the TextDirection and return it.
        return new TextDirections(dirList);
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
            discription = String.format("%s %s",
                    Translator.getInstance().getText("elevatorenter"),
                    routeLink.getNextFloor().toString());
        }
        else if(discription.contains("Stairs")){ // Stairs text
            discription = String.format("%s %s", Translator.getInstance().getText("stairenter"),
                    routeLink.getNextFloor().toString());
        }
        else if(discription.contains("Straight")){// going Straight
            discription = String.format("%s", Translator.getInstance().getText("straightline")); //haven't added distance yet
        }
        else{ // actually turning.
            discription=String.format("%s %s", discription, Translator.getInstance().getText("turning")); //haven't put distance in yet
        }

        if(routeLink.isEndFlag()) {
            discription= String.format("%s %s", discription, Translator.getInstance().getText("pathend"));
            //routeLink.getNext().getLongDescription());
        }

        // create and return the new formed Direction object.
        return new Direction(routeLink.getDistance(),
                routeLink.getStart().getCoordinate(),
                routeLink.getNext().getCoordinate(),
                discription,routeLink.getTurn());
    }

    private RouteLink combineFloorChange (RouteLink baseLink, RouteLink nextLink)
    {
        baseLink.setNextFloor(nextLink.getNextFloor());
        return baseLink;
    }

}
