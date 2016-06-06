package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Terrain} instances.
 */
public class TerrainEvent {
    public static class Save {
        private Terrain               terrain;

        /**
         * Creates a TerrainEvent.Save instance.
         *
         * @param terrain  a Terrain instance to act on
         */
        public Save(Terrain terrain) {
            this.terrain = terrain;
        }

        // Getters
        public Terrain getTerrain() {
            return terrain;
        }
    }

    public static class Delete {
        private Collection<DaoFilter> filters;

        /**
         * Creates a TerrainEvent.Delete instance.
         *
         * @param filters  filters to use to obtain Terrain instances to act on
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
         * Creates a TerrainEvent.Load instance.
         *
         * @param filters  filters to use to obtain Terrain instances to act on
         */
        public Load(Collection<DaoFilter> filters) {
            this.filters = filters;
        }

        // Getters
        public Collection<DaoFilter> getFilters() {
            return filters;
        }
    }

    public static class Saved extends SavedEvent<Terrain> {
        /**
         * @see SavedEvent#SavedEvent(boolean, Object)
         */
        public Saved(boolean successful, Terrain item) {
            super(successful, item);
        }
    }

    public static class Deleted extends DeletedEvent<Terrain> {
        /**
         * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
         */
        public Deleted(boolean successful, int numDeleted, Collection<Terrain> deleted) {
            super(successful, numDeleted, deleted);
        }
    }

    public static class Loaded extends LoadedEvent<Terrain> {
        /**
         * @see LoadedEvent#LoadedEvent(boolean, Collection)
         */
        public Loaded(boolean successful, Collection<Terrain> items) {
            super(successful, items);
        }
    }
}
