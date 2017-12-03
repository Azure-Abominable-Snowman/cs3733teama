package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.MapDataSource;

public abstract class MapCache implements MapDataSource {

    protected MapDataSource dataSource;

    public MapCache(MapDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
