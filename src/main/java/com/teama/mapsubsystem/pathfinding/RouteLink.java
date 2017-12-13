package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.translator.Translator;



public class RouteLink {

    private MapNode start;
    private MapNode next;
    private double turnAngle;
    private double pointingAngle = 0;
    private RouteLink lastLink;
    private double distance;
    private String textReturn;
    //private Icon icon;
    private Floor startFloor;
    private  Floor nextFloor; // Can't have an end floor since there is only a start and next, therefore no end

    private  boolean endFlag = false;
    private  TurnType turn;
    double pixToRealScaleFactor = 0.0929411764705882 ; //1870 pixel per 173.8m

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
            double dx = next.getCoordinate().getxCoord() - start.getCoordinate().getxCoord();
            double dy = next.getCoordinate().getyCoord() - start.getCoordinate().getyCoord();
            distance = ((double) Math.sqrt( dx*dx + dy*dy) )*pixToRealScaleFactor;
            turn=TurnType.START;
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
            distance = ((double) Math.sqrt( dx*dx + dy*dy) )*pixToRealScaleFactor;


            if(lastLink.getTurn().equals(TurnType.ELEVATOR) ){
                textReturn =Translator.getInstance().getText("exitElevator");
                turn=TurnType.INTONEWFLOOR;
                return;
            }
            else if(lastLink.getTurn().equals(TurnType.STAIR))
            {
                textReturn =Translator.getInstance().getText("exitStair");
                turn=TurnType.INTONEWFLOOR;
                return;
            }


            if(-15 <= turnAngle && turnAngle <= 15){
                textReturn = "Straight: ";
                turn=TurnType.STRAIGHT;
            }

            else if(-120 <= turnAngle && turnAngle <= -60){
                textReturn = String.format("%s", Translator.getInstance().getText("turnleft"));
                turn=TurnType.TURNLEFT;
            }

            else if(-60 < turnAngle && turnAngle < -15){
                textReturn = String.format("%s", Translator.getInstance().getText("turnleftslight"));
                turn=TurnType.TURNLEFTSLIGHT;
            }

            else if(-165 < turnAngle && turnAngle < -120){
                textReturn = String.format("%s", Translator.getInstance().getText("turnleftsharp"));
                turn=TurnType.TURENLEFTSHARP;
            }

            else if(60 <= turnAngle && turnAngle <= 120){
                textReturn = String.format("%s", Translator.getInstance().getText("turnright"));
                turn=TurnType.TURNRIGHT;
            }

            else if(15 < turnAngle && turnAngle < 60){
                textReturn = String.format("%s", Translator.getInstance().getText("turnrightslight"));
                turn=TurnType.TURNRIGHTSLIGHT;
            }

            else if(120 < turnAngle && turnAngle < 165){
                textReturn = String.format("%s", Translator.getInstance().getText("turnrightsharp"));
                turn=TurnType.TURNRIGHTSHARP;
            }
            else {
                textReturn = String.format("%s", Translator.getInstance().getText("reverse"));
                turn=TurnType.REVERSE;
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
                textReturn = String.format("%s", Translator.getInstance().getText("elevat"));
                turn=TurnType.ELEVATOR;
            }
            else{
                textReturn = String.format("%s", Translator.getInstance().getText("stairs"));
                turn=TurnType.STAIR;
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

    public void setNext(MapNode next) { this.next = next; }

    public boolean isEndFlag() {
        return endFlag;
    }

    public void setEndFlag(boolean endFlag) {
        this.endFlag = endFlag;
    }

    public TurnType getTurn() {
        return turn;
    }

    public void setTurn(TurnType turn) {
        this.turn = turn;
    }
}
