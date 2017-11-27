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
        ArrayList<RouteLink> turnPoints = new ArrayList<>(nodeList.size()-2);

        for(int i=1;i<nodeList.size()-1;++i)
        {
            RouteLink.add(new RouteLink(nodeList.get(i-1),nodeList.get(i),nodeList.get(i+1) ));
        }

        for(){
            //TODO Add to TextDirections, by shrinking the redundant text directions

            //TextualDirections outputDir = new

        }

        return outputDir;
    }





}
