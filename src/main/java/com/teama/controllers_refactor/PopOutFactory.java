package com.teama.controllers_refactor;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;

public class PopOutFactory {
    HashMap<PopOutType, String> popOutControllers;
    public PopOutFactory(){
       //TODO populate the hashamp with all of the necessary controllers
       popOutControllers= new HashMap<>();
    }
    public void addPopOut(PopOutType t, String file){
        popOutControllers.put(t, file);
    }
    public PopOutController makePopOut(PopOutType popOutType){
        //TODO have this method create the popOut and set its controller then return the controller
        try {
            String file = popOutControllers.get(popOutType);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
            loader.load();
            return loader.getController();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
