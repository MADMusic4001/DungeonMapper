package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link CellExitType} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class CellExitTypeEventPosting extends CellExitTypeEvent {
    public static class Save extends CellExitTypeEvent.Save {
        /**
         * @see CellExitTypeEvent.Save#Save(CellExitType)
         */
        public Save(CellExitType cellExitType) {
            super(cellExitType);
        }
    }

    public static class Delete extends CellExitTypeEvent.Delete {
        /**
         * @see CellExitTypeEvent.Delete#Delete(Collection)
         */
        public Delete(Collection<DaoFilter> filters) {
            super(filters);
        }
    }

    public static class Load extends CellExitTypeEvent.Load {
        /**
         * @see CellExitTypeEvent.Load#Load(Collection)
         */
        public Load(Collection<DaoFilter> filters) {
            super(filters);
        }
    }
}
