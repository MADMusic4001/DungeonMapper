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
package com.madmusic4001.dungeonmapper.view.di.modules;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.CellDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.CellExitTypeTypeDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.RegionDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.TerrainDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.WorldDaoSqlImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides instances of DAO SQL implementation classes for dependency injection.
 */
@Module(includes = ApplicationModule.class)
public class SqlDaoModule {
	@Provides @Singleton
	CellDao provideCellDao(DungeonMapperSqlHelper helper, CellExitTypeDao cellExitTypeDao, TerrainDao terrainDao) {
		return new CellDaoSqlImpl(helper, cellExitTypeDao, terrainDao);
	}

	@Provides @Singleton
	CellExitTypeDao provideCellExitDao(Context context, DungeonMapperSqlHelper helper) {
		return new CellExitTypeTypeDaoSqlImpl(context, helper);
	}

	@Provides @Singleton
	RegionDao provideRegionDao(DungeonMapperSqlHelper helper, WorldDao worldDao) {
		return new RegionDaoSqlImpl(helper, worldDao);
	}

	@Provides @Singleton
	TerrainDao provideTerrainDao(Context context, DungeonMapperSqlHelper helper) {
		return new TerrainDaoSqlImpl(context, helper);
	}

	@Provides @Singleton
	WorldDao provideWorldDao(DungeonMapperSqlHelper helper) {
		return new WorldDaoSqlImpl(helper);
	}
}
