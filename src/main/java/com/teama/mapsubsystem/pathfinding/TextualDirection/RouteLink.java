package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;

public class RouteLink {

    private MapNode start;
    private MapNode next;
    private double turnAngle;
    private double pointingAngle = 0;
    private RouteLink lastLink;
    private double distance;
    private String textReturn;
    private Floor startFloor;
    private  Floor nextFloor; // Can't have an end floor since there is only a start and next, therefore no end

    private  boolean endFlag = false;




    RouteLink(MapNode start, MapNode next, RouteLink lastLink) {

        this.start = start;
        this.next = next;
        this.lastLink=lastLink;
        pointingAngle = vectorAngle(start, next);

        if(lastLink != null) {

            turnAngle =  lastLink.getPointingAngle()- pointingAngle ;
            for(;turnAngle>=180;turnAngle-=360); // keep the angles below 180
            for (;turnAngle<-180;turnAngle+=360);   // keep the angles above -180
            genText();

        }
        else {
            textReturn = "Start";
        }

    }


    public double getPointingAngle()
    {
        return pointingAngle;
    }

    public double getTurnAngle() {
        return turnAngle;
    }

    public String getTextReturn() {
        return textReturn;
    }


    private void genText(){

        // in the case of the same floor.
        if ( start.getCoordinate().getLevel().equals(next.getCoordinate().getLevel())) {

            double dx = next.getCoordinate().getxCoord() - start.getCoordinate().getxCoord();
            double dy = next.getCoordinate().getyCoord() - start.getCoordinate().getyCoord();
            distance = (double) Math.sqrt( dx*dx + dy*dy);


            if(lastLink.getTextReturn().contains("Elevator") || lastLink.getTextReturn().contains("Stairs")){
                textReturn = "No Text";
                return;
            }


            if(-15 <= turnAngle && turnAngle <= 15){
                textReturn = "Straight: ";
            }

            else if(-120 <= turnAngle && turnAngle <= -60){
                textReturn = "Turn Left";
            }

            else if(-60 < turnAngle && turnAngle < -15){
                textReturn = "Turn Left Slightly";
            }

            else if(-165 < turnAngle && turnAngle < -120){
                textReturn = "Turn Left Sharply";
            }

            else if(60 <= turnAngle && turnAngle <= 120){
                textReturn = "Turn Right";
            }

            else if(15 < turnAngle && turnAngle < 60){
                textReturn = "Turn Right Slightly";
            }

            else if(120 < turnAngle && turnAngle < 165){
                textReturn = "Turn Right Sharply";
            }
            else {
                textReturn = "Moon Walk";
            }


        }
        else{
            //If across multiple floors
            //Split between stairs and els
            Integer eleNum;
            eleNum = 2;
            Integer stairNum;
            stairNum = 2;
            if(start.getNodeType().equals(NodeType.ELEV)){
                textReturn = "Enter Elevator";
            }
            else{
                textReturn = "Enter Stairs";
            }
            
        }
            startFloor = start.getCoordinate().getLevel();
            nextFloor = next.getCoordinate().getLevel();

        }


    private static float vectorAngle (MapNode start, MapNode end) {
        float angle = (float) Math.toDegrees(Math.atan2(start.getCoordinate().getyCoord() - end.getCoordinate().getyCoord(),
                end.getCoordinate().getxCoord() - start.getCoordinate().getxCoord()));

        for(;angle>=180;angle-=360); // keep the angles below 180
        for (;angle<-180;angle+=360);   // keep the angles above -180
        return angle;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Floor getStartFloor() {
        return startFloor;
    }

    public void setStartFloor(Floor startFloor) {
        this.startFloor = startFloor;
    }

    public Floor getNextFloor() {
        return nextFloor;
    }

    public void setNextFloor(Floor nextFloor) {
        this.nextFloor = nextFloor;
    }

    public MapNode getStart() {
        return start;
    }

    public MapNode getNext() {
        return next;
    }

    public boolean isEndFlag() {
        return endFlag;
    }

    public void setEndFlag(boolean endFlag) {
        this.endFlag = endFlag;
    }
}
