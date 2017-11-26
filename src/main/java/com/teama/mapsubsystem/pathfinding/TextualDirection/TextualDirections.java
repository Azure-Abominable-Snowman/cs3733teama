package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;

import java.util.ArrayList;

public class TextualDirections implements DirectionsGenerator {
    ArrayList<MapNode> nodeList;

    TextualDirections()
    {
        nodeList = new ArrayList<>();
    }

    @Override
    public TextDirections generateDirections(Path path) {
        nodeList = path.getNodes();
        ArrayList<turnPoint> turnPoints = new ArrayList<>(nodeList.size()-2);

        for(int i=1;i<nodeList.size()-1;++i)
        {
           turnPoints.add(new turnPoint(nodeList.get(i-1),nodeList.get(i),nodeList.get(i+1) ));
        }


        return null;
    }


    private class turnPoint{
        MapNode lastNode, thisNode, nextNode;
        float turnAngle;

        turnPoint(MapNode t,MapNode n ,MapNode l)
        {
            thisNode = t; nextNode = n; lastNode = l;
            turnAngle = turnAngle(thisNode,nextNode,lastNode);
            // TODO put these info into text.
        }

    }


    public static float turnAngle(MapNode start, MapNode thisNode, MapNode end) {
        float angle = AngleGenerator.positionAngle(start,thisNode) - AngleGenerator.positionAngle(thisNode,end);
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }





}
