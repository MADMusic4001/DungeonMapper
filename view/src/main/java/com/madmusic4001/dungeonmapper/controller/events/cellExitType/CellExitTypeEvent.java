package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing a request to take some action on a CellExitType instance or a collection of CellExitType instances.
 */
public class CellExitTypeEvent {
    public static class Save {
        private CellExitType          cellExitType;

        /**
         * Creates a CellExitTypeEvent.Save instance.
         *
         * @param cellExitType  a CellExitType instance to act on
         */
        public Save(CellExitType cellExitType) {
            this.cellExitType = cellExitType;
        }

        // Getters
        public CellExitType getCellExitType() {
            return cellExitType;
        }
    }

    public static class Delete {
        private Collection<DaoFilter> filters;

        /**
         * Creates a CellExitTypeEvent.Delete instance.
         *
         * @param filters  filters to use to obtain CellExitType instances to act on
         */
        public Delete(Collection<DaoFilter> filters) {
            this.filters = filters;
        }

        // Getters
        public Collection<DaoFilter> getFilters() {
            return filters;
        }
    }

    public static class Load {
        private Collection<DaoFilter> filters;

        /**
         * Creates a CellExitTypeEvent.Load instance.
         *
         * @param filters  filters to use to obtain CellExitType instances to act on
         */
        public Load(Collection<DaoFilter> filters) {
            this.filters = filters;
        }

        // Getters
        public Collection<DaoFilter> getFilters() {
            return filters;
        }
    }

    public static class Saved extends SavedEvent<CellExitType> {
        /**
         * @see SavedEvent#SavedEvent(boolean, Object)
         */
        public Saved(boolean successful, CellExitType item) {
            super(successful, item);
        }
    }

    public static class Deleted extends DeletedEvent<CellExitType> {
        /**
         * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
         */
        public Deleted(boolean successful, int numDeleted, Collection<CellExitType> deleted) {
            super(successful, numDeleted, deleted);
        }
    }

    public static class Loaded extends LoadedEvent<CellExitType> {
        /**
         * @see LoadedEvent#LoadedEvent(boolean, Collection)
         */
        public Loaded(boolean successful, Collection<CellExitType> items) {
            super(successful, items);
        }
    }
}
