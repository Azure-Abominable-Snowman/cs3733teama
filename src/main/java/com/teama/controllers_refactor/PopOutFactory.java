package com.teama.controllers_refactor;

public class PopOutFactory {

    public PopOutFactory(){

    }

    public PopOutController makePopOut(PopOutType popOutType){
        if(popOutType == null) {
            return null;
        }
        switch(popOutType) {
            case STAFFDIRECTORY:
                return new StaffPopOut();
            case EDITOR:
                return new EditorPopOut();
            case REQUESTS:
                return new RequestPopOut();
            case LOGIN:
                System.out.println("LOGIN SCREEN NOT YET IMPLEMENTED");
        }
        return null;
    }
}
