package com.teama.mapdrawingsubsystem;

import javafx.scene.image.Image;

public class ProxyMap implements HospitalMap {
    private Image map;
    private String path;

    public ProxyMap(String path) {
        this.path = path;
    }

    @Override
    public Image getMap() {
        if(map == null) {
            map = new Image(path);
        }
        return map;
    }
}
