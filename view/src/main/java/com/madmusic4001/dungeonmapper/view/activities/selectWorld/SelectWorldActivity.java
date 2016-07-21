/**
 * Copyright (C) 2014 MadMusic4001
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

package com.madmusic4001.dungeonmapper.view.activities.selectWorld;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.WorldEventHandler;
import com.madmusic4001.dungeonmapper.controller.events.DatabaseImportedEvent;
import com.madmusic4001.dungeonmapper.controller.events.ImportExportEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldEvent;
import com.madmusic4001.dungeonmapper.controller.handlers.WorldHandler;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.WorldDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.data.util.ComparatorUtils;
import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;
import com.madmusic4001.dungeonmapper.view.activities.FileSelectorDialogFragment;
import com.madmusic4001.dungeonmapper.view.activities.editTerrain.EditTerrainActivity;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.adapters.WorldListAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;
import com.madmusic4001.dungeonmapper.view.utils.BundleConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.madmusic4001.dungeonmapper.view.utils.IntentConstants.EDIT_WORLD_INTENT_WORLD_ID;

/**
 * Displays the list of saved {@link World} instances and allows the user to select an existing
 * {@link World} to edit, create a new {@link World}, or delete existing {@link World}.
 */
public class SelectWorldActivity extends Activity implements
		DbImportDialogFragment.ImportDialogListener,
		FileSelectorDialogFragment.FileSelectorDialogListener {
	@Inject
	protected WorldListAdapter  adapter;
//	@Inject
//	protected EventBus          eventBus;
	@Inject
	protected WorldHandler		worldHandler;
	@Inject
	protected WorldEventHandler worldEventHandler;
	@Inject
	protected FilterCreator     filterCreator;
	@Inject
	protected ComparatorUtils   comparatorUtils;
	private   ListView          listView;
	private   String            fileName;

	//**********************************************************************************************
	// Activity lifecycle event handlers
	//**********************************************************************************************

//	@Override
//	protected void onResume() {
//		super.onResume();
//		if(eventBus != null && !eventBus.isRegistered(this)) {
//			eventBus.register(this);
//		}
//	}

//	@Override
//	protected void onPause() {
//		if(eventBus != null) {
//			eventBus.unregister(this);
//		}
//		super.onPause();
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((DungeonMapperApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this)).injectInto(this);
//		if(!eventBus.isRegistered(this)) {
//			eventBus.register(this);
//		}
		setContentView(R.layout.select_world_layout);
		initListView();

		worldHandler.getWorlds(null)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Collection<World>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Collection<World> worlds) {
						String toastString;

						toastString = String.format(getString(R.string.toast_worlds_loaded), worlds.size());
						adapter.clear();
						adapter.addAll(worlds);
						adapter.notifyDataSetChanged();
						Toast.makeText(SelectWorldActivity.this, toastString, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_word_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.actionSettings:
				return true;
			case R.id.action_new_world:
				World newWorld = new World(getString(R.string.defaultWorldName));
				newWorld.setCreateTs(Calendar.getInstance());
				newWorld.setModifiedTs(newWorld.getCreateTs());
				newWorld.setRegionWidth(16);
				newWorld.setRegionHeight(16);
				newWorld.setOriginLocation(DataConstants.SOUTHWEST);
				newWorld.setOriginOffset(0);
				worldHandler.saveWorld(newWorld)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<World>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {
								Toast.makeText(SelectWorldActivity.this, "An exception occurred saving world", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onNext(World world) {
								String toastString;

								toastString = getString(R.string.toast_world_saved);
								adapter.add(world);
								adapter.notifyDataSetChanged();
								Toast.makeText(SelectWorldActivity.this, toastString, Toast.LENGTH_SHORT).show();
							}
						});
				return true;
			case R.id.action_manage_terrains:
				Intent intent = new Intent(this, EditTerrainActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_import:
				DialogFragment dialog;
				dialog = new FileSelectorDialogFragment();
				Bundle bundle = new Bundle();
				bundle.putString(BundleConstants.FILE_SELECTOR_FILTER,
								 DataConstants.WORLD_FILE_EXTENSION);
				dialog.setArguments(bundle);
				dialog.show(getFragmentManager(), "");
				return true;
			case R.id.action_export:
//				eventBus.post(new ImportExportEvent.ExportDatabase("temp_export_file_name"));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.select_world_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		World world;

		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

		switch (item.getItemId()) {
			case R.id.select_world_item_edit:
				world = (World)listView.getItemAtPosition(info.position);
				if(world != null) {
					editWorld(world);
					return true;
				}
				else {
					return false;
				}
			case R.id.select_world_item_delete:
				world = (World)listView.getItemAtPosition(info.position);
				Log.e("SelectWorldActivity", "Deleting world id " + world.getId());
				Collection<DaoFilter> filters = new ArrayList<>();
				filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
														  WorldDaoSqlImpl.WorldsContract._ID,
														  String.valueOf(world.getId())));
				worldHandler.deleteWorlds(filters)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Collection<World>>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onNext(Collection<World> worlds) {
								String toastString;

								toastString = getString(R.string.toast_worlds_deleted);
//								toastString = String.format(getString(R.string.toast_worlds_deleted), event.getNumDeleted());
								for(World world : worlds) {
									Log.e("SelectWorldActivity", "Deleted world id " + world.getId());
									adapter.remove(world);
								}
								adapter.notifyDataSetChanged();
								Toast.makeText(SelectWorldActivity.this, toastString, Toast.LENGTH_SHORT).show();
							}
						});
				return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	// <editor-fold desc="SelectWorldController.SelectWorldUpdateHandler interface implementation">

	/**
	 * Responds to a WorldSavedEvent by adding the saved world to the adapter and displaying a toast to the user if successful,
	 * otherwise displaying a toast and leaving the adapter unchanged.
	 *
	 * @param event  a WorldSavedEvent instance
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldSavedEvent(WorldEvent.Saved event) {
		String toastString;

		if(event.isSuccessful()) {
			toastString = getString(R.string.toast_world_saved);
			adapter.add(event.getItem());
			adapter.notifyDataSetChanged();
		}
		else {
			toastString = String.format(getString(R.string.toast_world_save_error), event.getItem().getName());
		}
		Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Responds to a LoadedEvent<World> by updating the adapter with the new collection of World instances and displaying a
	 * toast if successful, otherwise displaying a toast and clearing the adapter.
	 *
	 * @param event  a LoadedEvent<World> instance
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldsLoadedEvent(WorldEvent.Loaded event) {
		String toastString;

		if(event.isSuccessful()) {
			toastString = String.format(getString(R.string.toast_worlds_loaded), event.getItems().size());
			adapter.clear();
			adapter.addAll(event.getItems());
			adapter.notifyDataSetChanged();
		}
		else {
			toastString = getString(R.string.toast_worlds_load_error);
		}
		Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldDeletedEvent(WorldEvent.Deleted event) {
		String toastString;

		if(event.isSuccessful()) {
			toastString = String.format(getString(R.string.toast_worlds_deleted), event.getNumDeleted());
			for(World world : event.getDeleted()) {
				Log.e("SelectWorldActivity", "Deleted world id " + world.getId());
				adapter.remove(world);
			}
			adapter.notifyDataSetChanged();
		}
		else {
			toastString = getString(R.string.toast_worlds_deleted_error);
		}
		Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDatabaseExported(ImportExportEvent.DatabaseExported event) {
		DialogFragment dialog = new DbExportedDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BundleConstants.EXPORT_DIALOG_FILEPATH,
						 event.getFileName());
		dialog.setArguments(bundle);
		dialog.show(getFragmentManager(), "DbExportedDialogFragment");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDatabaseImported(DatabaseImportedEvent event) {
		Toast.makeText(this, R.string.toast_db_imported, Toast.LENGTH_LONG)
				.show();
	}
	// </editor-fold>

	// <editor-fold desc="DbImportDialogFragment.ImportDialogListener interface implementation">

	@Override
	public void onDialogCancelClick(DialogFragment dialog) {}

	@Override
	public void onDialogOverwriteClick(DialogFragment dialog) {
//		eventBus.post(new ImportExportEvent.ImportDatabase(fileName, true));
	}

	@Override
	public void onDialogKeepClick(DialogFragment dialog) {
//		eventBus.post(new ImportExportEvent.ImportDatabase(fileName, false));
	}
	// </editor-fold>

	// <editor-fold desc="FileSelectorDialogFragment.FileSelectorDialogListener interface implementation">

	@Override
	public void onFileSelected(String fileName) {
		Log.d(this.getClass().getName(), "In onFileSelected for " + fileName);
		this.fileName = fileName;
		DialogFragment dialog = new DbImportDialogFragment();
		dialog.show(getFragmentManager(), "DbImportDialogFragment");
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">

	private void initListView() {
		View headerView;

		listView = (ListView) findViewById(R.id.worldsList);
		headerView = getLayoutInflater().inflate(
				R.layout.name_timestamps_header, listView, false);
		listView.addHeaderView(headerView);

		// Create and set onClick methods for sorting the {@link World} list.
		headerView.findViewById(R.id.nameHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.sort(comparatorUtils.getWorldNameComparator());
			}
		});
		headerView.findViewById(R.id.createdHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.sort(comparatorUtils.getWorldCreateTsComparator());
			}
		});
		headerView.findViewById(R.id.modifiedHeader).setOnClickListener(new View.OnClickListener
				() {
			@Override
			public void onClick(View v) {
				adapter.sort(comparatorUtils.getWorldModifiedTsCompartor());
			}
		});

		listView.setAdapter(adapter);

		// Clicking a row in the listView will send the user to the edit world activity
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				World theWorld = (World) listView.getItemAtPosition(position);
				if (theWorld != null) {
					editWorld(theWorld);
				}
			}
		});
		registerForContextMenu(listView);
	}

	private void editWorld(@NonNull World world) {
		Intent intent = new Intent(getApplicationContext(), EditWorldActivity.class);
		intent.putExtra(EDIT_WORLD_INTENT_WORLD_ID, world.getId());
		startActivity(intent);
	}
	// </editor-fold>
}
