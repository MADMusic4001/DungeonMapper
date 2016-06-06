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

import com.madmusic4001.dungeonmapper.controller.events.world.WorldEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
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
	 * @param event  a {@link WorldEvent} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onWorldSaveEvent(WorldEvent.Save event) {
		saveWorld(event.getWorld());
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onWorldDeleteEvent(WorldEvent.Delete event) {
		deleteWorld(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onWorldsLoadEvent(WorldEvent.Load event) {
		loadWorlds(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldSaveEvent(WorldEventPosting.Save event) {
		saveWorld(event.getWorld());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldDeleteEvent(WorldEventPosting.Delete event) {
		deleteWorld(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldsLoadEvent(WorldEventPosting.Load event) {
		loadWorlds(event.getFilters());
	}

	private void saveWorld(World world) {
		eventBus.post(new WorldEvent.Saved(worldDao.save(world), world));
	}

	private void deleteWorld(Collection<DaoFilter> filters) {
		Collection<World> worldsDeleted = worldDao.load(filters);
		int deletedCount = worldDao.delete(filters);
		eventBus.post(new WorldEvent.Deleted(deletedCount >= 0, deletedCount, worldsDeleted));
	}

	private void loadWorlds(Collection<DaoFilter> filters) {
		Collection<World> worlds = null;
		boolean success = true;
		try {
			worlds = worldDao.load(filters);
		}
		catch(DaoException ex) {
			Log.e("WorldEventHandler", ex.getMessage(), ex);
			success = false;
		}
		eventBus.post(new WorldEvent.Loaded(success, worlds));
	}
}
