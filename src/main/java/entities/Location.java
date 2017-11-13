package entities;

public class Location {
    private int xCoord, yCoord;
    private String building;
    private String level;

    public Location(Location loc) {
        this(loc.getxCoord(), loc.getyCoord(), loc.getLevel(), loc.getBuilding());
    }

    public Location(int xCoord, int yCoord, String level, String building) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.level = level;
        this.building = building;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }
}
