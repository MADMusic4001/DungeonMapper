/**
 * Copyright (C) 2015 MadMusic4001
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
package com.madmusic4001.dungeonmapper.controller.events.cell;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Cell;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Cell} instances.
 */
public class CellEvent {
	public static class Save {
		private Cell                  cell;

		/**
		 * Creates a CellEvent.Save instance.
		 *
		 * @param cell  a Cell instance to act on
		 */
		public Save(Cell cell) {
			this.cell = cell;
		}

		// Getters
		public Cell getCell() {
			return cell;
		}
	}

	public static class Delete {
		private Collection<DaoFilter> filters;

		/**
		 * Creates a CellEvent.Delete instance.
		 *
		 * @param filters  filters to use to obtain Cell instances to act on
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
		 * Creates a CellEvent.Load instance.
		 *
		 * @param filters  filters to use to obtain Cell instances to act on
		 */
		public Load(Collection<DaoFilter> filters) {
			this.filters = filters;
		}

		// Getters
		public Collection<DaoFilter> getFilters() {
			return filters;
		}
	}

	/**
	 * Generic event notifying subscribers that a {@link Cell} instance was saved to persistent storage.
	 */
	public static class Saved extends SavedEvent<Cell> {
		/**
		 * @see SavedEvent#SavedEvent(boolean, Object)
		 */
		public Saved(boolean successful, Cell item) {
			super(successful, item);
		}
	}

	/**
	 * Event representing the results of a request to delete 1 or more {@link Cell} instances.
	 */
	public static class Deleted extends DeletedEvent<Cell> {
		/**
		 * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
		 */
		public Deleted(boolean success, int numDeleted, Collection<Cell> deleted) {
			super(success, numDeleted, deleted);
		}
	}

	/**
	 * Event representing the results of a request to load {@link Cell} instances from persistent storage.
	 */
	public static class Loaded extends LoadedEvent<Cell> {
		/**
		 * @see LoadedEvent#LoadedEvent(boolean, Collection)
		 */
		public Loaded(boolean successful, Collection<Cell> items) {
			super(successful, items);
		}
	}
}
