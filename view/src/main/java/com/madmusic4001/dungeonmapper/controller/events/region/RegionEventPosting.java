package com.madmusic4001.dungeonmapper.controller.events.region;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Region;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Region} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class RegionEventPosting extends RegionEvent {
    public static class Save extends RegionEvent.Save {
        public Save(Region region) {
            super(region);
        }
    }

    public static class Delete extends RegionEvent.Delete {
        public Delete(Collection<DaoFilter> filters) {
            super(filters);
        }
    }

    public static class Load extends RegionEvent.Load {
        public Load(Collection<DaoFilter> filters) {
            super(filters);
        }
    }
}
