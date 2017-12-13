package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.translator.Translator;


import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class TextualDirections implements DirectionsGenerator {
    private ArrayList<MapNode> pathNodeList;
    private ArrayList<RouteLink> routeLinks ;
    private ArrayList<Direction> dirList;
    private final double hightLightLength = 50; // overall length for both side of turn.

    public TextualDirections()
    {
        pathNodeList = new ArrayList<>();
    }

    @Override
    public TextDirections generateDirections(Path path) {



        pathNodeList = path.getNodes();
        routeLinks = new ArrayList<>(pathNodeList.size()-1);

        // group list of nodes into two point link structure and let RouteLink do the basic calculation.
        RouteLink lastLink  = new RouteLink(pathNodeList.get(0), pathNodeList.get(1),null);
        routeLinks.add(lastLink);
        RouteLink thisLink ;

        for(int i = 2; i< pathNodeList.size(); ++i)
        {
            thisLink = new RouteLink(pathNodeList.get(i-1), pathNodeList.get(i),lastLink);
            lastLink=thisLink;
            routeLinks.add(lastLink);
        }


         dirList = new ArrayList<>();
        // put RouteLinks into Direction format and refactor the same RouteLinks.
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
            // case of the link right out of the elevator don't need to ignore it anymore
            /*
            if( nextLink.getTurn().equals(TurnType.INTONEWFLOOR) )
            {
                thisTurn= nextLink;
                continue;
            }*/
            if (nextLink.getTurn().equals(TurnType.STRAIGHT)) {
                addLink(thisTurn, nextLink); // combine the next one into this.
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

    private static RouteLink addLink(RouteLink turnLink, RouteLink straightLink)
    {
        turnLink.setNext(straightLink.getNext());
        turnLink.setDistance(turnLink.getDistance()+ straightLink.getDistance());
        return turnLink;
    }

    private Direction formDirection (RouteLink routeLink , String nodeNameInto)
    {
        TurnType turn  = routeLink.getTurn();
        String discription = routeLink.getTextReturn();
        if (turn.equals(TurnType.ELEVATOR)){ // Elevator text
            discription = String.format("%s %s",
                    Translator.getInstance().getText("elevatorenter"),
                    routeLink.getNextFloor().toString());
        }
        else if (turn.equals(TurnType.STAIR)){ // Stairs text
            discription = String.format("%s %s", Translator.getInstance().getText("stairenter"),
                    routeLink.getNextFloor().toString());
        }
        else if(turn.equals(TurnType.STRAIGHT)){ // going Straight
            discription = String.format("%s", Translator.getInstance().getText("straightline"));
        }
        else{ // actually turning, going to new floor, into new floor.
            if(nodeNameInto.length()>1) {
                discription = String.format("%s %s %s", discription, Translator.getInstance().getText("turning"), nodeNameInto);
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
                discription,routeLink.getTurn(),
                grabHighLightList(routeLink));
    }

    private RouteLink combineFloorChange (RouteLink baseLink, RouteLink nextLink)
    {
        baseLink.setNextFloor(nextLink.getNextFloor());
        baseLink.setNext(nextLink.getNext());
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

    /**
     * The is the help generate the list of nodes for highlighting, by checking before and after the link.
     * the field hightLightLength is used to determent how much nodes to grab for each side, the combined length will
     * be longer then the this fields. if reached the start or end of path, then just exit.
     * @param turnLink the point for checking in both sides.
     * @return return the ArrayList of mapNodes that need to be highlighted.
     */
    private ArrayList<MapNode> grabHighLightList (RouteLink turnLink)
    {
        int turnIndex = pathNodeList.indexOf(turnLink.getStart()); // TODO possible point of error.
        ArrayList<MapNode> prevNodes = new ArrayList<>();
        ArrayList<MapNode> postNodes = new ArrayList<>();
        ArrayList<MapNode> outputNodes = new ArrayList<>();
        double prevLength=0, postLength=0;


        pathNodeList.indexOf(turnLink.getNext());

        // grab nodes that are before the turn point. added to the list in the backward order.
        for (int i = turnIndex;
             i>0 && prevLength<hightLightLength ;//&& pathNodeList.get(i).getId().equals(dirList.get(dirList.size()-1).getStart())// TODO broken ;
             i--)

        {
            MapNode prevNode = pathNodeList.get(i-1);
            prevNodes.add(prevNode);
            prevLength+=calDistance(prevNode,pathNodeList.get(i));
        }
        // grab nodes that are after the turn point. added to the list in the forward order

        //TODO fix this so the hightlight don't go too far into the next turn.
        for (int i = turnIndex
             ;(! pathNodeList.get(i).getId().equals(turnLink.getNext()) )&& postLength<hightLightLength
             ;++i)

        {
            MapNode postNode = pathNodeList.get(i+1);
            postNodes.add(postNode);
            postLength+=calDistance(postNode,pathNodeList.get(i));
        }

        // merge two list into one.
        // the prev nodes list first, and merge backwards, since size is one bigger then biggest node, need -1
        for(int i = prevNodes.size()-1;i>-1;--i)
        {
            outputNodes.add(prevNodes.get(i));
        }
        outputNodes.add(turnLink.getStart());     // put in the middle node as well.
        // then the post nodes list.
        for(int i=0;i<postNodes.size();++i)
        {
            outputNodes.add(postNodes.get(i));
        }
        return outputNodes;
    }

    protected int calDistance(MapNode n1, MapNode n2)
    {
        double x = (double) abs(n1.getCoordinate().getxCoord() - n2.getCoordinate().getxCoord());
        double y = (double) abs(n1.getCoordinate().getyCoord() - n2.getCoordinate().getxCoord());
        return (int) sqrt ( x*x+y*y ) ;
    }

}
