package com.teama.refresh;


import javafx.scene.Scene;
import javafx.stage.Stage;

public class MementoDesign {
    private Stage scene;

    public void setState(Stage state){
        this.scene = state;
    }

    public Memento saveState(){
        return new Memento(scene);
    }

    public void restore(Memento m){ scene = m.getState();
    }

    public static class Memento{
        private Stage scene;

        public Memento(Stage stateToSave){
            this.scene = stateToSave;
        }

        public Stage getState(){
            return scene;
        }
    }

}
