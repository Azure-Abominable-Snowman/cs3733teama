package com.teama.refresh;


import javafx.stage.Stage;

public class MementoDesign {
    public Stage scene = new Stage();

    public void setState(Stage state){
        this.scene = state;
    }

    public Memento saveState(){
        return new Memento(this.scene);
    }

    public void restore(Memento m){
        this.scene = m.getState();
    }

    public static class Memento{
        private final Stage scene;

        public Memento(Stage stateToSave){
            scene = stateToSave;
        }

        private Stage getState(){
            return scene;
        }
    }
}
