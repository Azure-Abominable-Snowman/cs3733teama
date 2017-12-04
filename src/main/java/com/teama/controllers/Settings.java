package com.teama.controllers;

import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Settings {

    private static class SettingsGetter {
        private static final Settings instance = new Settings();
    }

    public static synchronized Settings getInstance() {
        return Settings.SettingsGetter.instance;
    }

    private ObjectProperty<Path> currentDisplayedPath = new SimpleObjectProperty<>();

    public void setCurrentDisplayedPathProp(Path getCurrentDisplayedPath) {
        this.currentDisplayedPath.set(getCurrentDisplayedPath);
    }

    public ReadOnlyObjectProperty<Path> getCurrentDisplayedPathProp() {
        return currentDisplayedPath;
    }

}
