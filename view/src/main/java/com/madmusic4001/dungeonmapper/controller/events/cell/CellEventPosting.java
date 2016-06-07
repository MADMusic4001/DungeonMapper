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
package com.madmusic4001.dungeonmapper.controller.events.cell;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Cell;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link Cell} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class CellEventPosting extends CellEvent {
	public static class Save extends CellEvent.Save {
		/**
		 * @see CellEvent.Save#Save(Cell)
		 */
		public Save(Cell cell) {
			super(cell);
		}
	}

	public static class Delete extends CellEvent.Delete {
		/**
		 * @see CellEvent.Delete#Delete(Collection)
		 */
		public Delete(Collection<DaoFilter> filters) {
			super(filters);
		}
	}

	public static class Load extends CellEvent.Load {
		/**
		 * @see CellEvent.Load#Load(Collection)
		 */
		public Load(Collection<DaoFilter> filters) {
			super(filters);
		}
	}
}
