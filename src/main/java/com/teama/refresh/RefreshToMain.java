package com.teama.refresh;


import java.awt.event.InputEvent;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.Scene;


public class RefreshToMain {
    private List<MementoDesign.Memento> savedStates = new ArrayList<>();
    private Scene primaryScene;
    private Scene currScene;


    public Scene Refresh(InputEvent interaction) {
        this.primaryScene = primaryScene;

        MementoDesign mementoDesign = new MementoDesign();
        mementoDesign.setState(primaryScene);
        savedStates.add(mementoDesign.saveState());
        mementoDesign.setState(currScene);
        savedStates.add(mementoDesign.saveState());

        while (interaction == null) {
            try {
                interaction.wait(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        return primaryScene;
    }
}

