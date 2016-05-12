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
package com.madmusic4001.dungeonmapper.controller;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.managers.CellExitManager;
import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldRegionFragment;

import java.util.Collection;

import javax.inject.Inject;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/16/2015.
 */
public class EditWorldRegionController extends BaseController {
	private WorldManager    worldManager;
	private CellExitManager cellExitManager;
	private TerrainManager	terrainManager;

	/**
	 * Constructor for dependency injection.
	 */
	@Inject
	public EditWorldRegionController(EditWorldRegionFragment fragment, WorldManager worldManager,
									 CellExitManager cellExitManager,
									 TerrainManager terrainManager) {
		super(fragment.getActivity(), fragment);
		this.worldManager = worldManager;
		this.cellExitManager = cellExitManager;
		this.terrainManager = terrainManager;
	}

	/**
	 * Saves a {@link Region} instance to persistent storage.
	 *
	 * @param region the {@link Region} instance to save.
	 */
	public synchronized void saveRegion(final Region region, final String oldName) {

		new AsyncTask<Void, Void, Region>() {
			@Override
			protected Region doInBackground(Void... params) {
				return worldManager.saveRegion(region, oldName);
			}

			@Override
			protected void onPostExecute(Region region) {
				((EditWorldRegionUpdateHandler) getUpdateHandler()).onRegionSaved(region);
			}
		}.execute();
	}

	/**
	 * Loads all {@code CellExit} instances from storage.
	 */
	public synchronized void loadCellExits() {

		new AsyncTask<Void, Void, Collection<CellExit>>() {
			@Override
			protected Collection<CellExit> doInBackground(Void... params) {
				return cellExitManager.loadCellExits();
			}

			@Override
			protected void onPostExecute(Collection<CellExit> cellExits) {
				((EditWorldRegionUpdateHandler) getUpdateHandler())
						.onLoadCellExitsComplete(cellExits);
			}
		}.execute();
	}

	/**
	 * Loads all {@code Terrain} instances from storage.
	 */
	public synchronized void loadTerrains() {

		new AsyncTask<Void, Void, Collection<Terrain>>() {
			@Override
			protected Collection<Terrain> doInBackground(Void... params) {
				return terrainManager.loadTerrains();
			}

			@Override
			protected void onPostExecute(Collection<Terrain> terrains) {
				((EditWorldRegionUpdateHandler) getUpdateHandler())
						.onLoadTerrainsComplete(terrains);
			}
		}.execute();
	}

	/**
	 * Loads the {@link Region} instance to be displayed in the UI.
	 *
	 * @param worldName  the name of the {@link World} contaning the {@link Region} to load.
	 * @param regionName  the name of the {@link Region} instance to load.
	 */
	public synchronized void loadRegion(final String worldName, final String regionName) {

		new AsyncTask<Void, Void, Region>() {
			@Override
			protected Region doInBackground(Void... params) {
				Region region = worldManager.getRegionForWorld(worldName, regionName);
				if(region != null) {
					Log.d(this.getClass().getName(), "Loading cells for Region " + regionName + " in World " + worldName);
					worldManager.getCellsForRegion(region);
				}
				else {
					Log.e(this.getClass().getName(), "Failed to load Region + " + regionName + " for World " + worldName);
				}
				return region;
			}

			@Override
			protected void onPostExecute(Region region) {
				((EditWorldRegionUpdateHandler) getUpdateHandler()).onRegionLoaded(region);
			}
		}.execute();
	}

	// <editor-fold> dec="Callback interface declaration">
	public interface EditWorldRegionUpdateHandler extends BaseUpdateHandler {
		/**
		 * Sets the {@code Region} to be displayed in the UI.
		 *
		 * @param region the {@link Region} to display.
		 */
		void onRegionLoaded(@NonNull Region region);

		/**
		 * Notifies the implementer that the {@code Region} was saved.
		 *
		 * @param region  the {@link Region} that was saved.
		 */
		void onRegionSaved(Region region);

		/**
		 * Notifies the implementer that all {@code CellExit} instances have been loaded from
		 * storage.
		 *
		 * @param cellExits  the {@link Collection} of {@link CellExit} instances.
		 */
		void onLoadCellExitsComplete(Collection<CellExit> cellExits);

		/**
		 * Notifies the implementer that all {@code Terrain} instances have been loaded from storage.
		 *
		 * @param terrains  the {@link Collection} of {@link Terrain} instances.
		 */
		void onLoadTerrainsComplete(Collection<Terrain> terrains);
	}
	// </editor-fold>
}
