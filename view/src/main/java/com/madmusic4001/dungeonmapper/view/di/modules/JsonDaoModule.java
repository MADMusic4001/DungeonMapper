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

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.json.TerrainDaoJsonImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.json.WorldDaoJsonImpl;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.adapter.SparseArrayTypeAdapter;
import com.madmusic4001.dungeonmapper.data.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 5/19/2016.
 */
@Module
public class JsonDaoModule {
	@Provides @Singleton
	public Gson provideGson() {
		Type cellSparseArrayType = new TypeToken<SparseArray<Cell>>() {}.getType();
		Type bitmapSparseArrayType = new TypeToken<SparseArray<Bitmap>>() {}.getType();
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(cellSparseArrayType, new SparseArrayTypeAdapter<Cell>(Cell.class))
				.registerTypeAdapter(bitmapSparseArrayType, new SparseArrayTypeAdapter<Bitmap>(Bitmap.class))
				.create();
		return gson;
	}

	@Provides @Singleton
	public TerrainDao provideTerrainDao(FileUtils fileUtils) {
		return new TerrainDaoJsonImpl(fileUtils);
	}

	@Provides @Singleton
	public WorldDao provideWorldDao(FileUtils fileUtils) {
		return new WorldDaoJsonImpl(fileUtils);
	}
}
