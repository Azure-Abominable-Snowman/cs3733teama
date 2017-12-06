package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DirectionAdapter {
    private Direction d;
    private int stepNum;

    public DirectionAdapter(int stepNum, Direction d) {
        this.d = d;
        this.stepNum = stepNum;
    }

    public Text getDescription() {
        System.out.println("DES COL");
        Text t = new Text(d.getDescription());
        t.setWrappingWidth(127);
        return t;
    }

    public Text getDistance() {
        System.out.println("DISTANCE COL");
        Text t = new Text(Integer.toString((int)d.getLengthOfPath())+" meters");
        t.setWrappingWidth(61);
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    public Text getStepNum() {
        System.out.println("STEP COL");
        Text t = new Text(Integer.toString(stepNum));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    public ImageView getDirection() {
        System.out.println("GET DIRECTION "+d.getTurn());
        Image i = new Image("/icons/left.png");
        ImageView view = new ImageView();
        switch(d.getTurn()) {
            case TURNLEFT:
                i = new Image(getClass().getResourceAsStream("/icons/left.png"));
                break;
            case TURNLEFTSLIGHT:
                break;
            case TURENLEFTSHARP:
                break;
            case TURNRIGHT:
                break;
            case TURNRIGHTSLIGHT:
                break;
            case TURNRIGHTSHARP:
                break;
            case REVERSE:
                break;
            case STRAIGHT:
                i = new Image(getClass().getResourceAsStream("/icons/forward.png"));
                break;
            case START:
                break;
            case END:
                break;
            case INTONEWFLOOR:
                break;
            case ELEVATOR:
                break;
            case STAIR:
                break;
        }
        view.setImage(i);
        return view;
    }

    public Location getLocToFocus() {
        return d.getStart();
    }
}
