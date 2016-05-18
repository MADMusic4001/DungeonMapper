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

import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldPropsFragment;

import java.util.Collection;
import java.util.Comparator;

import javax.inject.Inject;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/14/2015.
 */
//public class EditWorldPropsController extends BaseController {
//
//	private WorldManager manager;
//
//	/**
//	 * Constructor for dependency injection.
//	 */
//	@Inject
//	public EditWorldPropsController(final EditWorldPropsFragment fragment,
//									final WorldManager worldManager) {
//		super(fragment.getActivity(), fragment);
//
////		this.manager = worldManager;
//	}
//
//	/**
//	 * Loads the {@link World} instance to be displayed in the UI.
//	 *
//	 * @param worldName the name of the {@link World} instance to retrieve.
//	 */
//	public synchronized void loadWorld(final String worldName) {
//
//		new AsyncTask<Void, Void, World>() {
//			@Override
//			protected World doInBackground(Void... params) {
//				return manager.getWorldWithName(worldName);
//			}
//
//			@Override
//			protected void onPostExecute(World world) {
//				((EditWorldPropsUpdateHandler) getUpdateHandler()).onWorldLoaded(world);
//			}
//		}.execute();
//	}
//
//	/**
//	 * Loads the {#link Region} instances for the given {@link World} from persistent storage.
//	 *
//	 * @param world the {@link World} whose {@link Region} instances are being requested.
//	 */
//	public synchronized void loadRegionsForWorld(final World world) {
//
//		new AsyncTask<Void, Void, Collection<Region>>() {
//			@Override
//			protected Collection<Region> doInBackground(Void... params) {
//				return manager.getRegionsForWorld(world);
//			}
//
//			@Override
//			protected void onPostExecute(Collection<Region> regions) {
//				((EditWorldPropsUpdateHandler) getUpdateHandler()).onRegionsLoaded(regions);
//			}
//		}.execute();
//	}
//
//	/**
//	 * Saves a {@link World} instance to persistent storage.
//	 *
//	 * @param world the {@link World} to save.
//	 */
//	public synchronized void saveWorld(final World world) {
//
//		new AsyncTask<Void, Void, World>() {
//			@Override
//			protected World doInBackground(Void... params) {
//				return manager.saveWorld(world);
//			}
//
//			@Override
//			protected void onPostExecute(World world) {
//				((EditWorldPropsUpdateHandler) getUpdateHandler()).onWorldSaved(world);
//			}
//		}.execute();
//	}
//
//	public synchronized void createRegion(final World world, final String regionName) {
//		new AsyncTask<Void, Void, Collection<Region>>() {
//			Region newRegion;
//
//			@Override
//			protected Collection<Region> doInBackground(Void... params) {
//				newRegion = manager.createNewRegion(world, regionName);
//				return manager.getRegionsForWorld(world);
//			}
//
//			@Override
//			protected void onPostExecute(Collection<Region> regions) {
//				((EditWorldPropsUpdateHandler)getUpdateHandler()).onRegionCreated(newRegion, regions);
//			}
//		}.execute();
//	}
//
//	public synchronized void deleteRegion(final Region region) {
//		new AsyncTask<Void, Void, Collection<Region>>() {
//
//			@Override
//			protected Collection<Region> doInBackground(Void... params) {
//				World world = region.getParent();
//				manager.deleteRegion(region);
//				return manager.getRegionsForWorld(world);
//			}
//
//			@Override
//			protected void onPostExecute(Collection<Region> regions) {
//				((EditWorldPropsUpdateHandler)getUpdateHandler()).onRegionDeleted(regions);
//			}
//		}.execute();
//	}
//
//	/**
//	 * Sort the {@link Region} list by {@link Region#getName()}
//	 */
//	public synchronized void sortRegionsByName() {
//		((EditWorldPropsUpdateHandler)getUpdateHandler()).onSortRegionList(
//				manager.getRegionNameComparator());
//	}
//
//	/**
//	 * Sort the {@link Region} list by {@link Region#getCreateTs()}
//	 */
//	public synchronized void sortRegionsByCreatedTs() {
//		((EditWorldPropsUpdateHandler)getUpdateHandler()).onSortRegionList(
//				manager.getRegionCreateTsComparator());
//	}
//
//	/**
//	 * Sort the {@link Region} list by {@link Region#getModifiedTs()}
//	 */
//	public synchronized void sortRegionsByModifiedTs() {
//		((EditWorldPropsUpdateHandler) getUpdateHandler()).onSortRegionList(
//				manager.getRegionModifiedTsCompartor());
//	}
//
//	/**
//	 * Interface to be implemented by the activity to allow the controller to request updates to
//	 * the UI.
//	 */
//	public interface EditWorldPropsUpdateHandler extends BaseUpdateHandler {
//		/**
//		 * Set the {@link World} to be displayed in the UI.
//		 *
//		 * @param world the {@link World} to display.
//		 */
//		void onWorldLoaded(World world);
//
//		/**
//		 * Notification to UI that the saveWorld request was completed.
//		 *
//		 * @param world  the {@link World} that was requested to be saved.
//		 */
//		void onWorldSaved(World world);
//
//		/**
//		 * Updates the collection of {@code Region} instances displayed to the user.
//		 *
//		 * @param regions  the new (@link Region) collection.
//		 */
//		void onRegionsLoaded(Collection<Region> regions);
//
//		/**
//		 * Sorts the {@code Region} list using the given {@code Comparator <Region>}.
//		 *
//		 * @param comparator  a {@link Comparator<Region>} to use to sort the list of {@link Region}
//		 *                         instances.
//		 */
//		void onSortRegionList(Comparator<Region> comparator);
//
//		/**
//		 * Create a new region and return the new region and updated region list.
//		 *
//		 * @param newRegion  the newly created {@link Region} instance.
//		 * @param regions  the new (@link Region) collection.
//		 */
//		void onRegionCreated(Region newRegion, Collection<Region> regions);
//
//		/**
//		 * Delete a region and return the updated region list.
//		 *
//		 * @param regions  the new (@link Region) collection.
//		 */
//		void onRegionDeleted(Collection<Region> regions);
//	}
//}
