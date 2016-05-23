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
import com.madmusic4001.dungeonmapper.controller.events.region.RegionPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionSavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionsDeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionsLoadedEvent;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Handles events requesting operations on {@link Region} instances with persistent storage.
 */
public class RegionEventHandler {
	private EventBus      eventBus;
	private RegionDao     regionDao;

	/**
	 * Creates a new RegionEventHandler instance.
	 *
	 * @param eventBus  a {@link EventBus} instance
	 * @param regionDao  a {@link RegionDao} instance
	 */
	@Inject
	public RegionEventHandler(EventBus eventBus, RegionDao regionDao) {
		this.eventBus = eventBus;
		this.regionDao = regionDao;
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a Region instance or instances. The work will be
	 * performed a separate thread from the poster.
	 *
	 * @param event  a {@link RegionPersistenceEvent} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onRegionPersistenceEvent(RegionPersistenceEvent event) {
		switch (event.getOperation()) {
			case SAVE:
				saveRegion(event);
				break;
			case DELETE:
				deleteRegions(event);
				break;
			case LOAD:
				loadRegions(event);
				break;
		}
	}

//	/**
//	 * Responds to requests to perform a persistent storage operation for a Region instance or instances. The work will be
//	 * performed in the same thread as the poster.
//	 *
//	 * @param event  a {@link RegionPersistenceEventPosting} instance containing the information need to complete the request
//	 */
//	@Subscribe(threadMode = ThreadMode.POSTING)
//	public void onRegionPersistenceEvent(RegionPersistenceEventPosting event) {
//		switch (event.getOperation()) {
//			case SAVE:
//				saveRegion(event);
//				break;
//			case DELETE:
//				deleteRegions(event);
//				break;
//			case LOAD:
//				loadRegions(event);
//				break;
//		}
//	}

	private void saveRegion(RegionPersistenceEvent event) {
		eventBus.post(new RegionSavedEvent(regionDao.save(event.getRegion()), event.getRegion()));
	}

	private void deleteRegions(RegionPersistenceEvent event) {
		Collection<Region> regionsDeleted = regionDao.load(event.getFilters());
		int deletedCount = regionDao.delete(event.getFilters());
		eventBus.post(new RegionsDeletedEvent(deletedCount >= 0, deletedCount, regionsDeleted));
	}

	private void loadRegions(RegionPersistenceEvent event) {
		Collection<Region> regions = null;
		boolean success = true;
		try {
			regions = regionDao.load(event.getFilters());
		}
		catch(DaoException ex) {
			Log.e("RegionEventHandler", ex.getMessage(), ex);
			success = false;
		}
		eventBus.post(new RegionsLoadedEvent(success, regions));
	}
}
