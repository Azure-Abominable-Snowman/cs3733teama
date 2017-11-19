package entities.PathRelated.BeamSearch;

import entities.MapNode;
import entities.PathRelated.Path;
import entities.PathRelated.PathAlgorithm;

public class BeamSearch implements PathAlgorithm {
    @Override
    public boolean verifyLocations() {
        return false;
    }

    @Override
    public Path generatePath(MapNode start, MapNode end) {
        return null;
    }
}
