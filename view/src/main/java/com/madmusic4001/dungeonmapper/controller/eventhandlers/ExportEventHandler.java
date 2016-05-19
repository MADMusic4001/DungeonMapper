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

import com.madmusic4001.dungeonmapper.controller.events.DatabaseExportedEvent;
import com.madmusic4001.dungeonmapper.controller.events.ExportDatabaseEvent;
import com.madmusic4001.dungeonmapper.data.dao.impl.json.WorldDaoJsonImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.WorldDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.World;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

/**
 * Handles requests to export the database to files.
 */
public class ExportEventHandler {
	private EventBus        eventBus;
	private WorldDaoSqlImpl worldDaoSql;
	private WorldDaoJsonImpl worldDaoJson;

	/**
	 * Creates a new ExportEventHandler instance with the given parameters.
	 *
	 * @param eventBus  a {@link EventBus} instance
	 * @param worldDaoSql  a {@link WorldDaoJsonImpl} instance
	 * @param worldDaoJson  a {@link WorldDaoJsonImpl} instance
	 */
	public ExportEventHandler(EventBus eventBus, WorldDaoSqlImpl worldDaoSql, WorldDaoJsonImpl worldDaoJson) {
		this.eventBus = eventBus;
		this.worldDaoSql = worldDaoSql;
		this.worldDaoJson = worldDaoJson;
	}

	/**
	 * Handles requests to export the database to file(s).
	 *
	 * @param event  an ExportDatabaseEvent
	 */
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onExportDatabaseEvent(ExportDatabaseEvent event) {
		Collection<World> worlds = worldDaoSql.load(null);
		boolean result = true;
		int worldCount = 0;
		for(World world : worlds) {
			result = worldDaoJson.save(world);
			if(!result) {
				break;
			}
			worldCount++;
		}
		eventBus.post(new DatabaseExportedEvent(result, worldCount, 0, 0, 0, 0));
	}
}
