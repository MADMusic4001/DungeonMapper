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
 * Event requesting an persistent storage operation to be performed on a World instance or instances.
 */
public class WorldPersistenceEvent {
	public enum Operation {
		SAVE,
		DELETE,
		LOAD
	}
	private Operation             operation;
	private World                 world;
	private Collection<DaoFilter> filters;

	/**
	 * Creates a new WorldPersistenceEvent instance.
	 *
	 * @param operation  the operation to perform
	 * @param world  the World instance to perform the operation on
	 * @param filters  the filters to use in the operation
	 */
	public WorldPersistenceEvent(Operation operation, World world, Collection<DaoFilter> filters) {
		this.operation = operation;
		this.world = world;
		this.filters = filters;
	}

	// Getters
	public Operation getOperation() {
		return operation;
	}
	public World getWorld() {
		return world;
	}
	public Collection<DaoFilter> getFilters() {
		return filters;
	}
}
