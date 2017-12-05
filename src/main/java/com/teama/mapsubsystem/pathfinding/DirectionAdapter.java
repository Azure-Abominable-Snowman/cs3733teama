package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;

public class DirectionAdapter {
    private Direction d;
    private int stepNum;

    public DirectionAdapter(int stepNum, Direction d) {
        this.d = d;
        this.stepNum = stepNum;
    }

    public String getDescription() {
        return d.getDescription();
    }

    public String getDistance() {
        return Integer.toString((int)d.getLengthOfPath());
    }

    public int getStepNum() { return stepNum; }
}
