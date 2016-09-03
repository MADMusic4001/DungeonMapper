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
package com.madmusic4001.dungeonmapper.controller.rxhandlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.entity.DatabaseObjectCount;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Creates reactive observable for requesting database import and export operations.
 */
public class ImportExportRxHandler {
	protected WorldDao worldDao;
	protected TerrainDao terrainDao;
	protected CellExitTypeDao cellExitTypeDao;

	/**
	 * Creates a new ImportExportRxHandler instance
	 *
	 * @param worldDao  a {@link WorldDao} instance to use to read and write World data to and from the database.
	 * @param terrainDao  a {@link TerrainDao} instance to use to read and write Terrain data to and from the database.
	 * @param cellExitTypeDao  a {@link CellExitTypeDao} instance to use to read and write CellExitTypeDao data to and from the database.
	 */
	@Inject
	public ImportExportRxHandler(WorldDao worldDao, TerrainDao terrainDao, CellExitTypeDao cellExitTypeDao) {
		this.worldDao = worldDao;
		this.terrainDao = terrainDao;
		this.cellExitTypeDao = cellExitTypeDao;
	}

	/**
	 * Creates an Observable that, when subscribed to, will import the DungeonMapper database from the file with the given filename,
	 * overwriting or keeping existing worlds as indicated.
	 *
	 * @param filename  the name of the file containing the data to import
	 * @param overwriteWorlds  true if an existing world with the same name as one being imported should be overwritten, otherwise false
	 * @return an Observable that, when subscribed to, will attempt to import the dungeonmapper database with the contents of the given
	 * file. The value supplied to the onNext method will be an integer representing the % completion of the import.
	 */
	public Observable<Integer> importDatabase(final String filename, boolean overwriteWorlds) {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				try {
					int read = 0;
					BufferedReader in = new BufferedReader(new FileReader(filename));
					Gson gson = new Gson();
					DatabaseObjectCount objectCount = gson.fromJson(in, DatabaseObjectCount.class);

					Type type = new TypeToken<Collection<Terrain>>(){}.getType();
					Collection<Terrain> terrains = gson.fromJson(in, type);
					read = terrains.size();
					subscriber.onNext((int)((read/objectCount.getTotalCount())*100));

					type = new TypeToken<Collection<CellExitType>>(){}.getType();
					Collection<CellExitType> cellExitTypes = gson.fromJson(in, type);
					read += cellExitTypes.size();
					subscriber.onNext((int)((read/objectCount.getTotalCount())*100));

					for(int i = 0; i < objectCount.getWorldCount(); i++) {
						World aWorld = gson.fromJson(in, World.class);
						read++;
						subscriber.onNext((int)((read/objectCount.getTotalCount())*100));
					}
					subscriber.onNext(100);
					subscriber.onCompleted();
				}
				catch (Exception e) {
					subscriber.onError(e);
				}
			}
		});
	}

	/**
	 * Creates an Observable that, when subscribed to, will export the DungeonMapper database to the file with the given filename.
	 *
	 * @param filename  the name of the file in which to write the database contents
	 * @return an Observable that, when subscribed to, will attempt to export the DungeonMapper database to the given
	 * file. The value supplied to the onNext method will be an integer representing the % completion of the export.
	 */
	public Observable<Integer> exportDatabase(final String filename) {
		return Observable.create(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(filename));
					Gson gson = new Gson();

					int numItems = worldDao.count(null) + 2;
					gson.toJson(numItems, out);

					gson.toJson(terrainDao.load(null), out);
					subscriber.onNext((int)((1/numItems)*100));

					gson.toJson(cellExitTypeDao.load(null), out);
					subscriber.onNext((int)((2/numItems)*100));

					Collection<World> worlds = worldDao.load(null);
					int i = 1;
					for(World world : worlds) {
						gson.toJson(world, out);
						subscriber.onNext((int)(((i++)+2/numItems)*100));
					}
					out.flush();
					out.close();
					subscriber.onCompleted();
				}
				catch (Exception e) {
					subscriber.onError(e);
				}
			}
		});
	}
}
