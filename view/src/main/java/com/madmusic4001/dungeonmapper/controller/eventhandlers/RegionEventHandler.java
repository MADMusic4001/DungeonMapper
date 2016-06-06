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

import com.madmusic4001.dungeonmapper.controller.events.region.RegionEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
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
	 * @param event  a {@link RegionEvent} instance containing the information need to complete the request
	 */
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onRegionSaveRequest(RegionEvent.Save event) {
		saveRegion(event.getRegion());
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onRegionDeleteRequest(RegionEvent.Delete event) {
		deleteRegions(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onRegionsLoadRequest(RegionEvent.Load event) {
		loadRegions(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onRegionSaveRequest(RegionEventPosting.Save event) {
		saveRegion(event.getRegion());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onRegionDeleteRequest(RegionEventPosting.Delete event) {
		deleteRegions(event.getFilters());
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onRegionsLoadRequest(RegionEventPosting.Load event) {
		loadRegions(event.getFilters());
	}

	private void saveRegion(Region region) {
		eventBus.post(new RegionEvent.Saved(regionDao.save(region), region));
	}

	private void deleteRegions(Collection<DaoFilter> filters) {
		Collection<Region> regionsDeleted = regionDao.load(filters);
		int deletedCount = regionDao.delete(filters);
		eventBus.post(new RegionEvent.Deleted(deletedCount >= 0, deletedCount, regionsDeleted));
	}

	private void loadRegions(Collection<DaoFilter> filters) {
		Collection<Region> regions = null;
		boolean success = true;
		try {
			regions = regionDao.load(filters);
		}
		catch(DaoException ex) {
			Log.e("RegionEventHandler", ex.getMessage(), ex);
			success = false;
		}
		eventBus.post(new RegionEvent.Loaded(success, regions));
	}
}
