package entities.PathRelated;

import entities.MapNode;

public interface PathGenerator {

    public boolean verifyLocations() ;
    public Path generatePath(MapNode start, MapNode end) ;
}
