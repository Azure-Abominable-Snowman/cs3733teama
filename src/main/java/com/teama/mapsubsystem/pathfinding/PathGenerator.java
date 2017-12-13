package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;

public class PathGenerator {
    private PathAlgorithm pathStrategy;

    public PathGenerator(PathAlgorithm pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public Path generatePath(MapNode start, MapNode end) {
        return pathStrategy.generatePath(start, end);
    }

    public Path generatePath(MapNode start, MapNode end, ArrayList<MapNode> diableNodes){
        return pathStrategy.generatePath(start,end,diableNodes);
    }
}
