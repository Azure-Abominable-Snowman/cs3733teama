package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;

public interface PathAlgorithm {
    Path generatePath(MapNode start, MapNode end) ;
}
