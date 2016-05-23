package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing the results of a request to delete 1 or more {@link Terrain} instances.
 */
public class TerrainsDeletedEvent extends DeletedEvent<Terrain> {
    /**
     * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
     */
    public TerrainsDeletedEvent(boolean successful, int numDeleted, Collection<Terrain> deleted) {
        super(successful, numDeleted, deleted);
    }
}
