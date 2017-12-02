package com.teama.controllers_refactor;

public class StaffPopOut extends PopOutController {
    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    public void initialize() {
        System.out.println("MAKE STAFF POP OUT CONTROLLER");
    }

    @Override
    public String getFXMLPath() {
        return "/TestPopOut.fxml";
    }
}
