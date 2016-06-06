package com.madmusic4001.dungeonmapper.controller.events.world;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

/**
 * Created by madanle on 6/6/16.
 */
public class WorldPersistenceResult {
    public static class Saved extends SavedEvent<World> {
        public Saved(boolean successful, World item) {
            super(successful, item);
        }
    }

    public static class Deleted extends DeletedEvent<World> {
        public Deleted(boolean successful, int numDeleted, Collection<World> deleted) {
            super(successful, numDeleted, deleted);
        }
    }

    public static class Loaded extends LoadedEvent<World> {
        public Loaded(boolean successful, Collection<World> items) {
            super(successful, items);
        }
    }
}
