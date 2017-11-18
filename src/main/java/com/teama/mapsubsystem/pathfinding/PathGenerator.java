package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.MapNode;

public interface PathGenerator {

    public boolean verifyLocations() ;
    public Path generatePath(MapNode start, MapNode end) ;
}
