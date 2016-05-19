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
import com.madmusic4001.dungeonmapper.controller.events.LoadedEvent;
import com.madmusic4001.dungeonmapper.controller.events.SavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldPersistentEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.WorldDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Singleton;

/**
 * Handles events requesting operations on {@link World} instances with persistent storage.
 */
@Singleton
public class WorldEventHandler {
	private EventBus eventBus;
	private WorldDao worldDao;
	private FilterCreator filterCreator;

	/**
	 * Creates a WorldEventHandler instance with the given parameters.
	 *
	 * @param eventBus  a {@link EventBus} instance
	 * @param worldDao  a {@link WorldDao} instance
	 * @param filterCreator  a {@link FilterCreator} instance
	 */
	public WorldEventHandler(EventBus eventBus, WorldDao worldDao, FilterCreator filterCreator) {
		this.eventBus = eventBus;
		this.worldDao = worldDao;
		this.filterCreator = filterCreator;
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a World instance or instances. The work will be
	 * performed a separate thread from the poster.
	 *
	 * @param event  a {@link WorldPersistenceEvent} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onWorldPersistenceEvent(WorldPersistenceEvent event) {
		switch (event.getOperation()) {
			case SAVE:
				saveWorld(event);
				break;
			case DELETE:
				deleteWorld(event);
				break;
			case LOAD:
				loadWorlds(event);
				break;
		}
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a World instance or instances. The work will be
	 * performed in the same thread as the poster.
	 *
	 * @param event  a {@link WorldPersistenceEvent} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldPersistenceEvent(WorldPersistentEventPosting event) {
		switch (event.getOperation()) {
			case SAVE:
				saveWorld(event);
				break;
			case DELETE:
				deleteWorld(event);
				break;
			case LOAD:
				loadWorlds(event);
				break;
		}
	}

	private void saveWorld(WorldPersistenceEvent event) {
		eventBus.post(new SavedEvent<>(worldDao.save(event.getWorld()), event.getWorld()));
	}

	private void deleteWorld(WorldPersistenceEvent event) {
		Collection<DaoFilter> filters = new ArrayList<>();
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
											 WorldDaoSqlImpl.WorldsContract._ID,
											 String.valueOf(event.getWorld().getId())));
		int deletedCount = worldDao.delete(filters);
		eventBus.post(new DeletedEvent<World>(deletedCount >= 0, deletedCount));
	}

	private void loadWorlds(WorldPersistenceEvent event) {
		Collection<World> worlds = null;
		boolean success = true;
		try {
			worlds = worldDao.load(event.getFilters());
		}
		catch(DaoException ex) {
			Log.e("WorldEventHandler", ex.getMessage(), ex);
			success = false;
		}
		eventBus.post(new LoadedEvent<>(success, worlds));
	}
}
