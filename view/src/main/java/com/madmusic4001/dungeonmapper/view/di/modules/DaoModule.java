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
package com.madmusic4001.dungeonmapper.view.di.modules;

import android.content.Context;

import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.CellExitDao;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.CellDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.CellExitDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.RegionDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.TerrainDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.WorldDaoSqlImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 7/4/2015.
 */
@Module(includes = ApplicationModule.class)
public class DaoModule {
	@Provides @Singleton
	CellDao provideCellDao(Context context, DungeonMapperSqlHelper helper, TerrainDao terrainDao, CellExitDao cellExitDao,
						   TerrainManager terrainManager) {
		return new CellDaoSqlImpl(context, helper, terrainDao, cellExitDao, terrainManager);
	}

	@Provides @Singleton
	CellExitDao provideCellExitDao(Context context, DungeonMapperSqlHelper helper) {
		return new CellExitDaoSqlImpl(context, helper);
	}

	@Provides @Singleton
	RegionDao provideRegionDao(Context context, DungeonMapperSqlHelper helper) {
		return new RegionDaoSqlImpl(context, helper);
	}

	@Provides @Singleton
	TerrainDao provideTerrainDao(Context context, DungeonMapperSqlHelper helper) {
		return new TerrainDaoSqlImpl(context, helper);
	}

	@Provides @Singleton
	WorldDao provideWorldDao(Context context, DungeonMapperSqlHelper helper) {
		return new WorldDaoSqlImpl(context, helper);
	}
}
