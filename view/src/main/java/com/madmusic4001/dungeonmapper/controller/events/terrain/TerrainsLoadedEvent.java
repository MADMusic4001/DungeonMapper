package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing the results of a request to load {@link Terrain} instances from persistent storage.
 */
public class TerrainsLoadedEvent extends LoadedEvent<Terrain> {
    /**
     * @see LoadedEvent#LoadedEvent(boolean, Collection)
     */
    public TerrainsLoadedEvent(boolean successful, Collection<Terrain> items) {
        super(successful, items);
    }
}
