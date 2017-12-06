package com.teama.controllers_refactor;

import javafx.beans.property.ReadOnlyDoubleProperty;

public class HelpPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);
    }

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {
        System.out.println("CLOSE HELP PAGE");
    }

    @Override
    public String getFXMLPath(){return "/HelpPopOut.fxml";}
}
