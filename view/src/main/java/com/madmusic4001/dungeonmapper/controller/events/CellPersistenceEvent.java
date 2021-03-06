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
package com.madmusic4001.dungeonmapper.controller.events;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Cell;

import java.util.Collection;

/**
 * Event representing a request to take some action on a Cell instance or a collection of Cell instances.
 */
public class CellPersistenceEvent {
	public enum Action {
		SAVE,
		DELETE,
		READ
	}
	private Action     action;
	private Cell       cell;
	private Collection<DaoFilter> filters;

	/**
	 * Creates a CellPersistenceEvent instance.
	 *
	 * @param action  the action being requested in the event
	 * @param cell  a Cell instance to act on
	 * @param filters  filters to use to obtain Cell instances to act on
	 */
	public CellPersistenceEvent(Action action, Cell cell, Collection<DaoFilter> filters) {
		this.action = action;
		this.cell = cell;
		this.filters = filters;
	}

	// Getters
	public Action getAction() {
		return action;
	}
	public Cell getCell() {
		return cell;
	}
	public Collection<DaoFilter> getFilters() {
		return filters;
	}
}
