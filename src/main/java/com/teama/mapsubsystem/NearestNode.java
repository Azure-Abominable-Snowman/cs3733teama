package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.Location;

public class NearestNode {
    public void findNearestNode (Location hitLocation)
    {
        MapSubsystem.getInstance().getVisibleFloorNodes(hitLocation.getLevel());

    }
}
