package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link CellExitType} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class CellExitTypePersistenceEventPosting extends CellExitTypePersistenceEvent {
    /**
     * @see CellExitTypePersistenceEvent#CellExitTypePersistenceEvent(Operation, CellExitType, Collection)
     */
    public CellExitTypePersistenceEventPosting(Operation operation, CellExitType cellExitType, Collection<DaoFilter> filters) {
        super(operation, cellExitType, filters);
    }
}
