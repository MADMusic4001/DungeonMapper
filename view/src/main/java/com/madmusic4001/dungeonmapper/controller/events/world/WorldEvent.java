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
package com.madmusic4001.dungeonmapper.controller.events.world;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

/**
 * Event requesting an persistent storage operation to be performed on a {@link World} instance or instances.
 */
public class WorldEvent {
	public static class Save {
		private World                 world;

		/**
		 * Creates a new WorldEvent.Save instance.
		 *
		 * @param world  the World instance to perform the operation on
		 */
		public Save(World world) {
			this.world = world;
		}

		// Getters
		public World getWorld() {
			return world;
		}
	}

	public static class Delete {
		private Collection<DaoFilter> filters;

		/**
		 * Creates a new WorldEvent.Delete instance.
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
		 * Creates a new WorldEvent.Load instance.
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

	public static class LoadById {
		int worldId;

		/**
		 * Creates a WorldEvent.LoadById instance.
		 *
		 * @param worldId  the id of the world to load.
         */
		public LoadById(int worldId) {
			this.worldId = worldId;
		}

		// Getters
		public int getWorldId() {
			return worldId;
		}
	}
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

	public static class SingleLoaded {
		private boolean successful;
		private World world;

		/**
		 * Creates a WorldEvent.SingleLoad instance.
		 *
		 * @param successful  true if a World instance was loaded successfully, otherwise false.
         * @param world  the World instance that was loaded or null if not successful.
         */
		public SingleLoaded(boolean successful, World world) {
			this.successful = successful;
			this.world = world;
		}

		// Getters
		public boolean isSuccessful() {
			return successful;
		}
		public World getWorld() {
			return world;
		}
	}
}
