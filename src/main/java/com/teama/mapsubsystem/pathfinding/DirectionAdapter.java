package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Location;
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
        Text t = new Text(d.getDescription());
        t.setWrappingWidth(127);
        return t;
    }

    public Text getDistance() {
        Text t = new Text(Integer.toString((int)d.getLengthOfPath())+" meters");
        t.setWrappingWidth(61);
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    public Text getStepNum() {
        Text t = new Text(Integer.toString(stepNum));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    public ImageView getDirection() {
        Image i = new Image("/icons/left.png");
        ImageView view = new ImageView();
        switch(d.getTurn()) {
            case TURNLEFT:
                i = new Image(getClass().getResourceAsStream("/icons/left.png"));
                break;
            case TURNLEFTSLIGHT:
                i = new Image(getClass().getResourceAsStream("/icons/slightly_left.png"));
                break;
            case TURENLEFTSHARP:
                i = new Image(getClass().getResourceAsStream("/icons/sharp_left.png"));
                break;
            case TURNRIGHT:
                i = new Image(getClass().getResourceAsStream("/icons/right.png"));
                break;
            case TURNRIGHTSLIGHT:
                i = new Image(getClass().getResourceAsStream("/icons/slight_right.png"));
                break;
            case TURNRIGHTSHARP:
                i = new Image(getClass().getResourceAsStream("/icons/sharp_right.png"));
                break;
            case REVERSE:
                i = new Image(getClass().getResourceAsStream("/icons/Uturn.png"));
                break;
            case STRAIGHT:
                i = new Image(getClass().getResourceAsStream("/icons/go.png"));
                break;
            case START:
                i = new Image(getClass().getResourceAsStream("/icons/end.png"));
                break;
            case END:
                i = new Image(getClass().getResourceAsStream("/icons/end.png"));
                break;
            case INTONEWFLOOR:
                break;
            case ELEVATOR:
                i = new Image(getClass().getResourceAsStream("/icons/elevator.png"));
                break;
            case STAIR:
                i = new Image(getClass().getResourceAsStream("/icons/stairs.png"));
                break;
        }
        view.setImage(i);
        view.setFitWidth(60);
        view.setFitHeight(60);
        return view;
    }

    public Location getLocToFocus() {
        return d.getStart();
    }
}
