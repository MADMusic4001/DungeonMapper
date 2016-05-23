package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing the results of a request to delete 1 or more {@link CellExitType} instances.
 */
public class CellExitTypesDeletedEvent extends DeletedEvent<CellExitType> {
    /**
     * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
     */
    public CellExitTypesDeletedEvent(boolean successful, int numDeleted, Collection<CellExitType> deleted) {
        super(successful, numDeleted, deleted);
    }
}
