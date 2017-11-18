package entities.PathRelated;

import entities.MapNode;

import java.util.HashMap;
import java.util.Queue;

public class BreathFirst implements PathGenerator {
   private Queue<MapNode> queue;
   private HashMap<String,MapNode> checkedPoints;

   //TODO make new helper class like knownPoint or expend knownPoint to be reusable?

    @Override
    public boolean verifyLocations() {
        return false;
    }

    @Override
    public Path generatePath(MapNode start, MapNode end) {
        return null;
    }
}
