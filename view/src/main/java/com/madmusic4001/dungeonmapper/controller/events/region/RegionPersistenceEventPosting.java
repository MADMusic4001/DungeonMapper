package com.madmusic4001.dungeonmapper.controller.events.region;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Region;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Region} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class RegionPersistenceEventPosting extends RegionPersistenceEvent {
    /**
     * @see RegionPersistenceEvent#RegionPersistenceEvent(Operation, Region, Collection)
     */
    public RegionPersistenceEventPosting(Operation operation, Region region, Collection<DaoFilter> filters) {
        super(operation, region, filters);
    }
}
