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

import com.madmusic4001.dungeonmapper.controller.events.WorldPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.WorldPersistentEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.World;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Singleton;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 5/12/2016.
 */
@Singleton
public class WorldEventHandler {
	private EventBus eventBus;
	private WorldDao worldDao;

	public WorldEventHandler(EventBus eventBus, WorldDao worldDao) {
		this.worldDao = worldDao;
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onWorldPersistenceEvent(WorldPersistenceEvent event) {
		switch (event.getAction()) {
			case SAVE:
				worldDao.save((World)event.getInformation());
				break;
			case DELETE:
				break;
			case READ:
				break;
		}
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onWorldPersistenceEvent(WorldPersistentEventPosting event) {
		switch (event.getAction()) {
			case SAVE:
				worldDao.save((World)event.getInformation());
				break;
			case DELETE:
				break;
			case READ:
				break;
		}
	}
}
