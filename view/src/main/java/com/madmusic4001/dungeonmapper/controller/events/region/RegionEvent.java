/**
 * Copyright (C) 2016 MadInnovations
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madmusic4001.dungeonmapper.controller.events.region;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Region;

import java.util.Collection;

/**
 * Event requesting an persistent storage operation to be performed on a {@link Region} instance or instances.
 */
public class RegionEvent {
	public static class Save {
		private Region region;

		/**
		 * Creates a new RegionPersistenceRequest.Save instance with the given parameters.
		 *
		 * @param region  the Region instance to perform the operation on
		 */
		public Save(Region region) {
			this.region = region;
		}

		// Getters
		public Region getRegion() {
			return region;
		}
	}

	public static class Delete {
		private Collection<DaoFilter> filters;

		/**
		 * Creates a new RegionPersistenceRequest.Delete instance with the given parameters.
		 *
		 * @param filters  the filters to use in the operation
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
		 * Creates a new RegionPersistenceRequest.Load instance with the given parameters.
		 *
		 * @param filters  the filters to use in the operation
		 */
		public Load(Collection<DaoFilter> filters) {
			this.filters = filters;
		}

		// Getters
		public Collection<DaoFilter> getFilters() {
			return filters;
		}
	}

	public static class Saved extends SavedEvent<Region> {
		public Saved(boolean successful, Region item) {
			super(successful, item);
		}
	}

	public static class Deleted extends DeletedEvent<Region> {
		public Deleted(boolean successful, int numDeleted, Collection<Region> deleted) {
			super(successful, numDeleted, deleted);
		}
	}

	public static class Loaded extends LoadedEvent<Region> {
		public Loaded(boolean successful, Collection<Region> items) {
			super(successful, items);
		}
	}

	public static class Selected {
		private Region region;
		private boolean switchFragments;

		/**
		 * Creates a new RegionSelectedEvent with the given parmeters.
		 *
		 * @param region  the selected Region instance
		 * @param switchFragments  true if fragment switching should ne done, otherwise false
		 */
		public Selected(Region region, boolean switchFragments) {
			this.region = region;
			this.switchFragments = switchFragments;
		}

		// Getters
		public Region getRegion() {
			return region;
		}
		public boolean isSwitchFragments() {
			return switchFragments;
		}
	}

	public static class SizeChanged {
		private int width;
		private int height;

		/**
		 * Creates a new RegionSizeChangedEvent instance.
		 *
		 * @param height  the new region height
		 * @param width  the new region width;
		 */
		public SizeChanged(int height, int width) {
			this.height = height;
			this.width = width;
			if(width <= 0 || height <= 0) {
				throw new IllegalArgumentException("Width and height must be > 0");
			}
		}

		// Getters
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
	}
}
