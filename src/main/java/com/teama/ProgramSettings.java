package com.teama;

import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ProgramSettings {

    private static class SettingsGetter {
        private static final ProgramSettings instance = new ProgramSettings();
    }

    public static synchronized ProgramSettings getInstance() {
        return ProgramSettings.SettingsGetter.instance;
    }

    private ObjectProperty<Path> currentDisplayedPath = new SimpleObjectProperty<>();

    public void setCurrentDisplayedPathProp(Path getCurrentDisplayedPath) {
        this.currentDisplayedPath.set(getCurrentDisplayedPath);
    }

    public ReadOnlyObjectProperty<Path> getCurrentDisplayedPathProp() {
        return currentDisplayedPath;
    }

}
