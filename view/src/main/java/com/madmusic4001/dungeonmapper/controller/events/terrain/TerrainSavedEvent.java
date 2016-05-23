package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

/**
 * Generic event notifying subscribers that a {@link Terrain} instance was saved to persistent storage.
 */
public class TerrainSavedEvent extends SavedEvent<Terrain> {
    /**
     * @see SavedEvent#SavedEvent(boolean, Object)
     */
    public TerrainSavedEvent(boolean successful, Terrain item) {
        super(successful, item);
    }
}
