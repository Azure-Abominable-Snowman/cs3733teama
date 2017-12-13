package com.teama.refresh;


import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class RefreshToMain {
    private List<MementoDesign.Memento> savedStates = new ArrayList<>();
    private Stage primaryStage;
    private Stage currStage = new Stage();


    private Stage setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Main Screen"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Brigham and Women's Hospital Kiosk");
            primaryStage.setScene(scene);
            primaryStage.show();
            }   catch (IOException e){
                e.printStackTrace();
        }
        return primaryStage;
    }


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
            //return mementoDesign.restore(savedStates.get(0));
        }
    }
}
