package com.teama.mapsubsystem.pathfinding.DijkstrasFamily;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.KnownPoint;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;

import java.util.ArrayList;

public abstract class DijkstrasTemplate implements PathAlgorithm {
    public abstract Path generatePath(MapNode start, MapNode end);
    public abstract Path generatePath(MapNode start, MapNode end, ArrayList<MapNode> disableNodes);
    protected abstract Path formatOutput(ArrayList<MapNode> finalPath);

}
