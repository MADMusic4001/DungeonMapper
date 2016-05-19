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

import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.cell.CellPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.cell.CellPersistenceEventPosting;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.CellDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
	private FilterCreator filterCreator;

	@Inject
	public CellEventHandler(EventBus eventBus, CellDao cellDao, FilterCreator filterCreator) {
		this.eventBus = eventBus;
		this.cellDao = cellDao;
		this.filterCreator = filterCreator;
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onCellPersistenceEvent(CellPersistenceEvent event) {
		switch (event.getOperation()) {
			case SAVE:
				saveCell(event);
				break;
			case DELETE:
				deleteCells(event);
				break;
			case LOAD:
				loadCells(event);
				break;
		}
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onCellPersistenceEvent(CellPersistenceEventPosting event) {
		switch (event.getOperation()) {
			case SAVE:
				saveCell(event);
				break;
			case DELETE:
				deleteCells(event);
				break;
			case LOAD:
				loadCells(event);
				break;
		}
	}

	private void saveCell(CellPersistenceEvent event) {
		eventBus.post(new SavedEvent(cellDao.save(event.getCell()), event.getCell()));
	}

	private void deleteCells(CellPersistenceEvent event) {
		Collection<DaoFilter> filters = new ArrayList<>();
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
												  CellDaoSqlImpl.CellsContract._ID,
												  String.valueOf(event.getCell().getId())));
		int deletedCount = cellDao.delete(filters);
		eventBus.post(new DeletedEvent(deletedCount >= 0, deletedCount));
	}

	private void loadCells(CellPersistenceEvent event) {
		Collection<Cell> cells = null;
		boolean success = true;
		try {
			cells = cellDao.load(event.getFilters());
		}
		catch(DaoException ex) {
			Log.e("CellEventHandler", ex.getMessage(), ex);
			success = false;
		}
		eventBus.post(new LoadedEvent<Cell>(success, cells));
	}
}
