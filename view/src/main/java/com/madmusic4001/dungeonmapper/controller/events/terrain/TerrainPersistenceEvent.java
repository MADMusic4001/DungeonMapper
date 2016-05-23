package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Terrain} instances.
 */
public class TerrainPersistenceEvent {
    public enum Operation {
        SAVE,
        DELETE,
        LOAD
    }
    private Operation             operation;
    private Terrain               terrain;
    private Collection<DaoFilter> filters;

    /**
     * Creates a TerrainPersistenceEvent instance.
     *
     * @param operation  the action being requested in the event
     * @param terrain  a Terrain instance to act on
     * @param filters  filters to use to obtain Terrain instances to act on
     */
    public TerrainPersistenceEvent(Operation operation, Terrain terrain, Collection<DaoFilter> filters) {
        this.operation = operation;
        this.terrain = terrain;
        this.filters = filters;
    }

    // Getters
    public Operation getOperation() {
        return operation;
    }
    public Terrain getTerrain() {
        return terrain;
    }
    public Collection<DaoFilter> getFilters() {
        return filters;
    }
}
