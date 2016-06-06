package com.madmusic4001.dungeonmapper.controller.events.terrain;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Terrain} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class TerrainEventPosting extends TerrainEvent {
    public static class Save extends TerrainEvent.Save {
        public Save(Terrain terrain) {
            super(terrain);
        }
    }

    public static class Delete extends TerrainEvent.Delete {
        public Delete(Collection<DaoFilter> filters) {
            super(filters);
        }
    }

    public static class Load extends TerrainEvent.Load {
        public Load(Collection<DaoFilter> filters) {
            super(filters);
        }
    }
}
