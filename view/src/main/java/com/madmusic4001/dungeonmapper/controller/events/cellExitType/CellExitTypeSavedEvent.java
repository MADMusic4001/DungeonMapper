package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

/**
 * Generic event notifying subscribers that a {@link CellExitType} instance was saved to persistent storage.
 */
public class CellExitTypeSavedEvent extends SavedEvent<CellExitType> {
    /**
     * @see SavedEvent#SavedEvent(boolean, Object)
     */
    public CellExitTypeSavedEvent(boolean successful, CellExitType item) {
        super(successful, item);
    }
}
