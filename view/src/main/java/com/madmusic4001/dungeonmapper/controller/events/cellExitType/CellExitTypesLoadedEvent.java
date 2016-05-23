package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing the results of a request to load {@link CellExitType} instances from persistent storage.
 */
public class CellExitTypesLoadedEvent extends LoadedEvent<CellExitType> {
    /**
     * @see LoadedEvent#LoadedEvent(boolean, Collection)
     */
    public CellExitTypesLoadedEvent(boolean successful, Collection<CellExitType> items) {
        super(successful, items);
    }
}
