package com.teama.mapsubsystem.data;

import javafx.scene.input.MouseEvent;

public class Location {
    private int xCoord, yCoord;
    private String building;
    private Floor level;

    public Location(Location loc) {
        this(loc.getxCoord(), loc.getyCoord(), loc.getLevel(), loc.getBuilding());
    }

    public Location(int xCoord, int yCoord, Floor level, String building) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.level = level;
        this.building = building;
    }

    /**
     * Location from a mouse event and a floor
     * @param e
     */
    public Location(MouseEvent e, Floor floor) {
        this((int)e.getX(), (int)e.getY(), floor, "Unknown");
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public Floor getLevel() {
        return level;
    }

    public String getBuilding() {
        return building;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Location){
            Location l = (Location) obj;
            if(this.xCoord == l.xCoord && this.yCoord == l.yCoord &&
                    this.level.equals(l.level) && this.building.equals(l.building)){
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString(){
        return xCoord + ", " + yCoord;
    }

}
