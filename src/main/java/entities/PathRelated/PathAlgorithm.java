package entities.PathRelated;

import entities.MapNode;

public interface PathAlgorithm {

    public boolean verifyLocations() ;
    public Path generatePath(MapNode start, MapNode end) ;
}
