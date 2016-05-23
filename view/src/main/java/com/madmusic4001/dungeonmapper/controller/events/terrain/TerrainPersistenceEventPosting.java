package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Terrain} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class TerrainPersistenceEventPosting extends TerrainPersistenceEvent {
    /**
     * @see TerrainPersistenceEvent#TerrainPersistenceEvent(Operation, Terrain, Collection)
     */
    public TerrainPersistenceEventPosting(Operation operation, Terrain terrain, Collection<DaoFilter> filters) {
        super(operation, terrain, filters);
    }
}
