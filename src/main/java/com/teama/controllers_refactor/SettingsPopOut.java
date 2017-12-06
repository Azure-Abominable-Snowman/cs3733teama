package com.teama.controllers_refactor;

import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import javafx.beans.property.ReadOnlyDoubleProperty;

public class SettingsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    private InterpreterStaff insertStaff;

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
        System.out.println("CLOSE SETTING");
    }

    @Override
    public String getFXMLPath(){return "/SettingsPopOut.fxml";}
}
