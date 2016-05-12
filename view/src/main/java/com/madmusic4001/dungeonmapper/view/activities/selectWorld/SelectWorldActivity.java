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
import com.madmusic4001.dungeonmapper.controller.SelectWorldController;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;
import com.madmusic4001.dungeonmapper.view.activities.editTerrain.EditTerrainActivity;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.activities.FileSelectorDialogFragment;
import com.madmusic4001.dungeonmapper.view.adapters.WorldListAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;
import com.madmusic4001.dungeonmapper.view.utils.BundleConstants;

import java.util.Collection;
import java.util.Comparator;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.view.utils.IntentConstants
		.EDIT_WORLD_INTENT_WORLD_NAME;

/**
 * Displays the list of saved {@link World} instances and allows the user to select an existing
 * {@link World} to edit, create a new {@link World}, or delete existing {@link World}.
 */
public class SelectWorldActivity extends Activity implements
		SelectWorldController.SelectWorldUpdateHandler,
		DbImportDialogFragment.ImportDialogListener,
		FileSelectorDialogFragment.FileSelectorDialogListener {
	@Inject
	protected WorldListAdapter      adapter;
	@Inject
	protected SelectWorldController controller;
	private ListView 				listView;
	private String					fileName;

	//**********************************************************************************************
	// Activity lifecycle event handlers
	//**********************************************************************************************

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((DungeonMapperApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this)).injectInto(this);
		setContentView(R.layout.select_world_layout);
		initListView();
		controller.loadWorlds();
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
				controller.createWorld(null);
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
				controller.exportDatabase();
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
				if(world != null) {
					controller.deleteWorld(world);
					return true;
				}
				else {
					return false;
				}
		default:
			return super.onContextItemSelected(item);
		}
	}

	// <editor-fold desc="SelectWorldController.SelectWorldUpdateHandler interface implementation">

	@Override
	public void updateWorldList(Collection<World> worlds) {
		adapter.clear();
		adapter.addAll(worlds);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void sortWorldList(Comparator<World> comparator) {
		adapter.sort(comparator);
	}

	@Override
	public void worldsExported(String exportFileName) {
		DialogFragment dialog = new DbExportedDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BundleConstants.EXPORT_DIALOG_FILEPATH,
						 exportFileName);
		dialog.setArguments(bundle);
		dialog.show(getFragmentManager(), "DbExportedDialogFragment");
	}

	@Override
	public void worldsImported(boolean success) {
		Toast.makeText(this, R.string.toast_db_imported, Toast.LENGTH_LONG)
				.show();
	}
	// </editor-fold>

	// <editor-fold desc="DbImportDialogFragment.ImportDialogListener interface implementation">

	@Override
	public void onDialogCancelClick(DialogFragment dialog) {}

	@Override
	public void onDialogOverwriteClick(DialogFragment dialog) {
		controller.importDatabase(fileName, true);
	}

	@Override
	public void onDialogKeepClick(DialogFragment dialog) {
		controller.importDatabase(fileName, false);
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
				controller.sortWorldsByName();
			}
		});
		headerView.findViewById(R.id.createdHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				controller.sortWorldsByCreatedTs();
			}
		});
		headerView.findViewById(R.id.modifiedHeader).setOnClickListener(new View.OnClickListener
				() {
			@Override
			public void onClick(View v) {
				controller.sortWorldsByModifiedTs();
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
		intent.putExtra(EDIT_WORLD_INTENT_WORLD_NAME, world.getName());
		startActivity(intent);
	}
	// </editor-fold>
}
