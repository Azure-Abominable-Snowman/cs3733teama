package com.teama.refresh;

import java.awt.event.InputEvent;
import java.util.List;
import java.util.ArrayList;

import com.teama.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class RefreshToMain {
    private List<MementoDesign.Memento> savedStates = new ArrayList<>();
    private Stage primaryStage;
    private Stage currStage = new Stage();


    public void Refresh(InputEvent interaction) {
        MementoDesign mementoDesign = new MementoDesign();
        mementoDesign.setState(primaryStage);
        savedStates.add(mementoDesign.saveState());
        mementoDesign.setState(currStage);
        savedStates.add(mementoDesign.saveState());

        while (interaction == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            // return mementoDesign.restore(savedStates.get(0));

        }
    }
}
