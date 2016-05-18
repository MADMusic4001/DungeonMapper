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
package com.madmusic4001.dungeonmapper.controller.eventhandlers;

import com.madmusic4001.dungeonmapper.controller.events.CellDeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.CellPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.CellSavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.CellsLoadedEvent;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.entity.Cell;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles {@link Cell} related events.
 */
@Singleton
public class CellEventHandler {
	private EventBus eventBus;
	private CellDao cellDao;

	@Inject
	public CellEventHandler(EventBus eventBus, CellDao cellDao) {
		this.eventBus = eventBus;
		this.cellDao = cellDao;
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onCellPersistenceEvent(CellPersistenceEvent event) {
		boolean result;

		switch (event.getAction()) {
			case SAVE:
				result = cellDao.save(event.getCell());
				eventBus.post(new CellSavedEvent(result, event.getCell()));
				break;
			case DELETE:
				int numAffected = cellDao.delete(event.getFilters());
				eventBus.post(new CellDeletedEvent(numAffected >= 0, numAffected));
				break;
			case READ:
				Collection<Cell> cells = cellDao.load(event.getFilters());
				eventBus.post(new CellsLoadedEvent(cells));
				break;
		}
	}
}
