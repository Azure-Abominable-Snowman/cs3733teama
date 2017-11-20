package com.teama.mapsubsystem.data;

import com.teama.mapsubsystem.MapSubsystem;

import java.util.Iterator;
import java.util.Set;

//AHALL0100G

public class DatabaseUUID {

    private static String team = "A";

    public static String generateID(Enum<NodeType> type, String currentFloor) {
        return generateID((NodeType)type, currentFloor, MapSubsystem.getInstance().getFloorNodes(currentFloor).keySet());
    }

    public static String generateID(NodeType type, String currentFloor, Set<String> floorIds) {
        int maxId = 0;
        if(currentFloor.length() < 2) {
            currentFloor = "0"+currentFloor;
        }
        Iterator<String> ids = floorIds.iterator();
        // Find the next ID number
        if(ids.hasNext()) {
            for(String id = ids.next(); ids.hasNext(); id = ids.next()) {
                System.out.println(type.name());
                if(id.substring(0, 1).equals(team) && id.substring(1, 5).equals(type.name()) && id.substring(8, 10).equals(currentFloor)) {
                    int curId = Integer.parseInt(id.substring(5, 8));
                    if (curId > maxId) {
                        maxId = curId;
                    }
                }
            }
        }
        maxId++; // one after the biggest one
        String paddedId = Integer.toString(maxId);
        int len = paddedId.length();
        for(int i = 0; i < 3-len; i++) {
            paddedId = "0" + paddedId;
        }
        return team+type.name()+paddedId+currentFloor;
    }

    public static String generateID(String startNodeId, String endNodeId) {
        return startNodeId+"_"+endNodeId;
    }
}
