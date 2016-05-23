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
import com.madmusic4001.dungeonmapper.controller.events.world.WorldPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldPersistenceEventPosting;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldSavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldsDeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldsLoadedEvent;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import javax.inject.Singleton;

/**
 * Handles events requesting operations on {@link World} instances with persistent storage.
 */
@Singleton
public class WorldEventHandler {
	private EventBus eventBus;
	private WorldDao worldDao;

	/**
	 * Creates a WorldEventHandler instance with the given parameters.
	 *
	 * @param eventBus  a {@link EventBus} instance
	 * @param worldDao  a {@link WorldDao} instance
	 */
	public WorldEventHandler(EventBus eventBus, WorldDao worldDao) {
		this.eventBus = eventBus;
		this.worldDao = worldDao;
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
	 * @param event  a {@link WorldPersistenceEventPosting} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldPersistenceEvent(WorldPersistenceEventPosting event) {
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
		eventBus.post(new WorldSavedEvent(worldDao.save(event.getWorld()), event.getWorld()));
	}

	private void deleteWorld(WorldPersistenceEvent event) {
		Collection<World> worldsDeleted = worldDao.load(event.getFilters());
		int deletedCount = worldDao.delete(event.getFilters());
		eventBus.post(new WorldsDeletedEvent(deletedCount >= 0, deletedCount, worldsDeleted));
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
		eventBus.post(new WorldsLoadedEvent(success, worlds));
	}
}
