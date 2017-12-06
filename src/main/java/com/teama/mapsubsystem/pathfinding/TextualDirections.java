package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
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
        String intoNodeName =""; // this is to hold the long description of the best along all nodes.
        NodeType oldNodeType = NodeType.BATH; // this is a flag that go with the intoNodeName for getting the right one.

        // The start of rest of all combining job of directions.
        for(int i=2;i<routeLinks.size();i++) {
            nextLink=routeLinks.get(i);
            if(holdNodeName(nextLink.getNext(),oldNodeType))
            {
                oldNodeType = nextLink.getNext().getNodeType();
                // ignore the case of only a bathroom link in the middle.
                if(! ( oldNodeType.equals(NodeType.BATH)|| oldNodeType.equals((NodeType.REST))) ) {
                    intoNodeName = nextLink.getNext().getLongDescription();
                }
            } // This is to record any good node name comming after the turn node.

            // Link for both nodes are in elevator.
            if( (thisTurn.getTurn().equals(TurnType.ELEVATOR) || thisTurn.getTurn().equals(TurnType.STAIR))){
                if((nextLink.getTurn().equals(TurnType.ELEVATOR) || nextLink.getTurn().equals(TurnType.STAIR))){
                    combineFloorChange(thisTurn,nextLink); // combine the next one into this.
                    continue;
                }
            }
            // case of the link right out of the elevator, ignore this for now.
            if( nextLink.getTurn().equals(TurnType.INTONEWFLOOR) )
            {
                thisTurn= nextLink;
                continue;
            }
            if (nextLink.getTurn().equals(TurnType.STRAIGHT)) {
                addDistance(thisTurn, nextLink); // combine the next one into this.

                continue;
            }
            // condenced turn and floor change case.
            dirList.add(formDirection(thisTurn,intoNodeName));
            oldNodeType = NodeType.BATH;// reset the oldNodeType
            intoNodeName="";
            thisTurn=nextLink;
        }
        // create the end link.
        thisTurn.setEndFlag(true);
        dirList.add(formDirection(thisTurn,intoNodeName));
        //warp the list into the TextDirection and return it.
        return new TextDirections(dirList);
    }


    ///////////////////////  Helpers /////////////////////

    private static RouteLink addDistance(RouteLink turnLink, RouteLink straightLink)
    {
        turnLink.setDistance(turnLink.getDistance()+ straightLink.getDistance());
        return turnLink;
    }

    private Direction formDirection (RouteLink routeLink , String nodeNameInto)
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
            discription = String.format("%s", Translator.getInstance().getText("straightline"));
        }
        else{ // actually turning.
            if(nodeNameInto.length()>1) {
                discription = String.format("%s %s %s", discription, Translator.getInstance().getText("turning"), nodeNameInto);
                // TODO change the work with turning key from "and walk for" to "into"
            }
            else discription = String.format("%s", discription); // in the case we don't have a walk into situation.

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


    /**
     * This will look at the next node type and try to find first the best kind of node.
     * @param nextNode the next node that might have the good node type.
     * @param oldNodetype   the recorded node type.
     * @return true if nextNode is has a better node type then the old one.
     */
    private boolean holdNodeName (MapNode nextNode, NodeType oldNodetype )
    {
        int nextTypeRank= rankNodeType(nextNode.getNodeType());
        int oldTypeRank = rankNodeType(oldNodetype);
        if(nextTypeRank>oldTypeRank) return true;
        else return false;
    }

    /**
     * This is the return a integer of ranked node type, only work with holdNodeName function.
     * @param type the one need to be ranked
     * @return the rank of the type, higher the better.
     */
    private int rankNodeType (NodeType type)
    {
        switch (type)
        {
            case BATH:
            case REST: return 1;
            case HALL:
            case ELEV:
            case STAI: return 2;
            case DEPT:
            case LABS:
            case INFO:
            case CONF:
            case EXIT:
            case RETL:
            case SERV: return 3;
            default: return 0; // this the case of no NodeType recorded.
        }
    }


}
