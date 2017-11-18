package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.MapNode;

public class PathGenerator {
    private PathAlgorithm pathStrategy;
    private DisplayPath animationStyle;

    public PathGenerator(PathAlgorithm pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public Path generatePath(MapNode start, MapNode end) {
        return pathStrategy.generatePath(start, end);
    }

    public void setAnimationStyle(DisplayPath animationStyle) {
        this.animationStyle = animationStyle;
    }

    public void displayOnScreen(MapDisplay display) {
        animationStyle.displayOnScreen(display);
    }
}
