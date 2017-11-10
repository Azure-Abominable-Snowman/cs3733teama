package entities.PathRelated;

import entities.MapNode;

public class AStar implements  PathGenerator{

    @Override
    public boolean verifyLocations() {
        return false;
    }

    @Override
    public Path generatePath(MapNode start, MapNode end) {
        return null;
    }
}
