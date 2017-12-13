package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;
import java.util.HashMap;

public interface PathAlgorithm {
    Path generatePath(MapNode start, MapNode end) ;
    Path generatePath(MapNode start, MapNode end, ArrayList<MapNode> disableNodes);
}
