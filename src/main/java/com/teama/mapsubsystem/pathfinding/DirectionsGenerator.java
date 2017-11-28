package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;

public interface DirectionsGenerator {
    TextDirections generateDirections(Path path);
}
