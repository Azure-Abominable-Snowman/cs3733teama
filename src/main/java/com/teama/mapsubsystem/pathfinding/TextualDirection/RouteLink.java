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

    private String textReturn;


    private RouteLink(MapNode start, MapNode next, RouteLink lastLink) {

        this.start = start;
        this.next = next;

        pointingAngle = AngleGenerator.positionAngle(start, next);

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

        if ( start.getCoordinate().getLevel().equals(next.getCoordinate().getLevel())) {

            /*
            if (start.getCoordinate().getxCoord() != end.getCoordinate().getxCoord()
                    || start.getCoordinate().getyCoord() != end.getCoordinate().getyCoord()) {
            }
            */


            /*
             if(turnAngle > 0){
             turnAngle -= 360;
             }

            */

            //TODO Add the angle if else generator

            if(-15 <= turnAngle && turnAngle <= 15){
                textReturn = "Straight: + dis";
            }

            else if(-105 <= turnAngle && turnAngle <= -75){
                textReturn = " Turn Left";
            }

            else if(-75 < turnAngle && turnAngle < -15){
                textReturn = " Turn Left Slightly";
            }

            else if(-165 < turnAngle && turnAngle < -105){
                textReturn = " Turn Left Sharply";
            }

            else if(75 <= turnAngle && turnAngle <= 105){
                textReturn = " Turn Right";
            }

            else if(15 < turnAngle && turnAngle < 75){
                textReturn = " Turn Right Slightly";
            }

            else if(105 < turnAngle && turnAngle < 165){
                textReturn = " Turn Right Sharply";
            }






        }
        else{
            //If across multiple floors
            //Split between stairs and els
        }

    }





}
