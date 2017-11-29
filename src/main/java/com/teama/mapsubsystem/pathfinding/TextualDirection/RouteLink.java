package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;

public class RouteLink {

    private MapNode thisNode, nextNode;
    private int link;
    private MapNode start;
    private MapNode next;
    private double turnAngle;
    private double pointingAngle;
    private RouteLink lastLink;
    private double distance;
    private String textReturn;


    RouteLink(MapNode start, MapNode next, RouteLink lastLink) {

        this.start = start;
        this.next = next;

        pointingAngle = AngleGenerator.vectorAngle(start, next);

        if(lastLink != null) {

            turnAngle =  pointingAngle - lastLink.getPointingAngle();

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

            double dx = start.getCoordinate().getxCoord() - next.getCoordinate().getxCoord();
            double dy = start.getCoordinate().getyCoord() - next.getCoordinate().getyCoord();
            distance = (double) Math.sqrt( dx*dx + dy*dy);

            if(-15 <= turnAngle && turnAngle <= 15){
                textReturn = "Straight: ";
            }

            else if(-105 <= turnAngle && turnAngle <= -75){
                textReturn = "Turn Left";
            }

            else if(-75 < turnAngle && turnAngle < -15){
                textReturn = "Turn Left Slightly";
            }

            else if(-165 < turnAngle && turnAngle < -105){
                textReturn = "Turn Left Sharply";
            }

            else if(75 <= turnAngle && turnAngle <= 105){
                textReturn = "Turn Right";
            }

            else if(15 < turnAngle && turnAngle < 75){
                textReturn = "Turn Right Slightly";
            }

            else if(105 < turnAngle && turnAngle < 165){
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
            if(start.getCoordinate().getLevel().toString().equals("Elevator")){
                textReturn = "Enter Elevator" + eleNum + "and go to Floor" + floor;
            }

            else if(start.getCoordinate().getLevel().toString().equals("Stairs")){
                textReturn = "Enter Stairs" + stairNum + "and go to Floor" + floor;
            }


        }
    }


    //TODO Delete Extra code below

    private Floor floor = Floor.G; // Default floor
    //private String name = "";

    public enum Floor {
        SB, B, G, ONE, TWO, THREE;
    }




    private static RouteLink addDistance(RouteLink turnLink, RouteLink straightLink)
    {
        return null;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }





}
