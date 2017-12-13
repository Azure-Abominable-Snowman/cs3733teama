package com.teama.refresh;


import javafx.scene.Scene;

public class MementoDesign {
    public Scene scene;

    public void setState(Scene state){
        this.scene = state;
    }

    public Memento saveState(){
        return new Memento(this.scene);
    }

    public void restore(Memento m){
        this.scene = m.getState();
    }

    public static class Memento{
        private final Scene scene;

        public Memento(Scene stateToSave){
            scene = stateToSave;
        }

        private Scene getState(){
            return scene;
        }
    }

}
