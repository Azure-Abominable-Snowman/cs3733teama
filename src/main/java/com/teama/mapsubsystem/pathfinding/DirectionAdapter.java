package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
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
        t.setWrappingWidth(220);
        return t;
    }

    public Text getDistance() {
        Text t = new Text(Integer.toString((int)d.getLengthOfPath())+" meters");
        t.setWrappingWidth(62);
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    public Text getStepNum() {
        Text t = new Text(Integer.toString(stepNum));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
}
