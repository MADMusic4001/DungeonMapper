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

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

/**
 * Event representing a request to take some action on one or more {@link World} instances and that needs to
 * execute in the same thread as the posting thread.
 */
public class WorldEventPosting extends WorldEvent {
	public static class Save extends WorldEvent.Save {
		public Save(World world) {
			super(world);
		}
	}

	public static class Delete extends WorldEvent.Delete {
		public Delete(Collection<DaoFilter> filters) {
			super(filters);
		}
	}

	public static class Load extends WorldEvent.Load {
		public Load(Collection<DaoFilter> filters) {
			super(filters);
		}
	}
//	/**
//	 * @see WorldPersistenceEvent#WorldPersistenceEvent(Operation, World, Collection)
//     */
//	public WorldPersistenceEventPosting(Operation operation, World world, Collection<DaoFilter> filters) {
//		super(operation, world, filters);
//	}
}
