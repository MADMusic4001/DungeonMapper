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

//import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
//import com.madmusic4001.dungeonmapper.data.entity.World;
//import com.madmusic4001.dungeonmapper.view.activities.selectWorld.SelectWorldActivity;
//
//import java.util.Collection;
//import java.util.Comparator;
//
//import javax.inject.Inject;
//
///**
// * ${CLASS_DESCRIPTION}
// *
// * @author Mark
// * Created 7/4/2015.
// */
//public class SelectWorldController extends BaseController {
//	private WorldManager manager;
//
//	/**
//	 * Constructor for dependency injecttion.
//	 */
//	@Inject
//	public SelectWorldController(final SelectWorldActivity activity, final WorldManager manager) {
//		super(activity, activity);
//		this.manager = manager;
//	}
//
//	public WorldManager getManager() {
//		return manager;
//	}
//
//	/**
//	 * Asynchronously loads all worlds from persistent storage. Executes {@link
//	 * SelectWorldController.SelectWorldUpdateHandler#updateWorldList} method upon completion.
//	 */
//	public synchronized void loadWorlds() {
////		new AsyncTask<Void, Void, Collection<World>>() {
////			@Override
////			protected Collection<World> doInBackground(Void... params) {
////				return manager.getAllWorlds(false);
////			}
////
////			@Override
////			protected void onPostExecute(Collection<World> worlds) {
////				((SelectWorldUpdateHandler)getUpdateHandler()).updateWorldList(worlds);
////			}
////		}.execute();
//	}
//
//	public synchronized void createWorld(final String worldName) {
////		new AsyncTask<Void, Void, Collection<World>>() {
////
////			@Override
////			protected Collection<World> doInBackground(Void... params) {
////				manager.createNewWorld(worldName);
////				return manager.getAllWorlds(false);
////			}
////
////			@Override
////			protected void onPostExecute(Collection<World> worlds) {
////				((SelectWorldUpdateHandler)getUpdateHandler()).updateWorldList(worlds);
////			}
////		}.execute();
//	}
//
//	/**
//	 * Deletes a {@link World} from persistent storage.
//	 *
//	 * @param world the {@link World} to delete.
//	 */
//	public synchronized void deleteWorld(final World world) {
////		new AsyncTask<Void, Void, Collection<World>>() {
////			@Override
////			protected Collection<World> doInBackground(Void... params) {
////				return manager.deleteWorld(world);
////			}
////
////			@Override
////			protected void onPostExecute(Collection<World> worlds) {
////				((SelectWorldUpdateHandler)getUpdateHandler()).updateWorldList(worlds);
////			}
////		}.execute();
//	}
//
//	/**
//	 * Saves changes to an existing {@link World} to persistent storage.
//	 *
//	 * @param world the {@link World} to update.
//	 */
//	public synchronized void updateWorld(final World world) {
////		new AsyncTask<Void, Void, Collection<World>>() {
////			@Override
////			protected Collection<World> doInBackground(Void... params) {
////				Log.d(this.getClass().getSimpleName(), "Saving World: " + world);
////				manager.saveWorld(world);
////				return manager.getAllWorlds(false);
////			}
////
////			@Override
////			protected void onPostExecute(Collection<World> worlds) {
////				((SelectWorldUpdateHandler)getUpdateHandler()).updateWorldList(worlds);
////			}
////		}.execute();
//	}
//
//	public synchronized void exportDatabase() {
////		new AsyncTask<Void, Void, String>() {
////			@Override
////			protected String doInBackground(Void... params) {
////				Log.d(this.getClass().getSimpleName(), "Exporting database");
////				return manager.exportDatabase();
////			}
////
////			@Override
////			protected void onPostExecute(String fileName) {
////				Log.d(this.getClass().getName(), "World successfully saved to " + fileName);
////				((SelectWorldUpdateHandler)getUpdateHandler()).worldsExported(fileName);
////			}
////		}.execute();
//	}
//
//	public synchronized void importDatabase(final String filePath, final boolean overwrite) {
////		new AsyncTask<Void, Void, Boolean>() {
////			@Override
////			protected Boolean doInBackground(Void... params) {
////				Log.d(this.getClass().getSimpleName(), "Importing database");
////				return manager.importDatabase(filePath, overwrite);
////			}
////
////			@Override
////			protected void onPostExecute(Boolean success) {
////				((SelectWorldUpdateHandler)getUpdateHandler()).worldsImported(success);
////			}
////		}.execute();
//	}
//
//	/**
//	 * Sort the {@link World} list by {@link World#getName()}
//	 */
//	public synchronized void sortWorldsByName() {
////		((SelectWorldUpdateHandler)getUpdateHandler()).sortWorldList(manager.getWorldNameComparator());
//	}
//
//	/**
//	 * Sort the {@link World} list by {@link World#getCreateTs()}
//	 */
//	public synchronized void sortWorldsByCreatedTs() {
////		((SelectWorldUpdateHandler)getUpdateHandler()).sortWorldList(
////				manager.getWorldCreateTsComparator());
//	}
//
//	/**
//	 * Sort the {@link World} list by {@link World#getModifiedTs()}
//	 */
//	public synchronized void sortWorldsByModifiedTs() {
////		((SelectWorldUpdateHandler) getUpdateHandler()).sortWorldList(
////				manager.getWorldModifiedTsCompartor());
//	}
//
//	/**
//	 * Interface to be implemented by the activity to allow the controller to request updates to
//	 * the UI.
//	 */
//	public interface SelectWorldUpdateHandler extends BaseUpdateHandler {
//		/**
//		 * Update the collection of {@link World} instances displayed to the user.
//		 *
//		 * @param worlds the new (@link World) collection.
//		 */
//		void updateWorldList(Collection<World> worlds);
//
//		/**
//		 * Sorts the {@link World} list using the given {@link Comparator<World>}.
//		 *
//		 * @param comparator a {@link Comparator<World>} to use to sort the list of {@link World}
//		 *                         instances.
//		 */
//		void sortWorldList(Comparator<World> comparator);
//
//		/**
//		 * Informs the user that the world database was sucessfully exported.
//		 *
//		 * @param exportFileName the name of the file containing the exported database.
//		 */
//		void worldsExported(String exportFileName);
//
//		/**
//		 * Informs the user of the success or failure of the database import.
//		 *
//		 * @param success true if the import was successfull otherwise false.
//		 */
//		void worldsImported(boolean success);
//	}
//}
