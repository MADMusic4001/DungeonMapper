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

package com.madmusic4001.dungeonmapper.view.activities.editWorld;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.CellExitTypeEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.RegionEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.TerrainEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.WorldEventHandler;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldEvent;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;
import com.madmusic4001.dungeonmapper.view.di.components.ActivityComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.view.utils.IntentConstants.EDIT_WORLD_INTENT_WORLD_ID;

/**
 *
 */
public class EditWorldActivity extends Activity {
	@Inject
	protected EventBus              	eventBus;
	@Inject
	protected WorldEventHandler			worldEventHandler;
	@Inject
	protected RegionEventHandler    	regionEventHandler;
	@Inject
	protected CellExitTypeEventHandler	cellExitTypeEventHandler;
	@Inject
	protected TerrainEventHandler		terrainEventHandler;
	private ActivityComponent			activityComponent;
	private EditWorldPropsFragment		propsFragment;
	private EditWorldRegionFragment 	regionFragment;
	private int                     	worldId = DataConstants.UNINITIALIZED;
	private int  						selectedRegionId = DataConstants.UNINITIALIZED;

	// <editor-fold desc="Activity lifecycle event handlers">
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activityComponent = ((DungeonMapperApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);
		if(!eventBus.isRegistered(this)) {
			eventBus.register(this);
		}

		if (savedInstanceState != null) {
			worldId = savedInstanceState.getInt(DataConstants.CURRENT_WORLD_ID);
			selectedRegionId = savedInstanceState.getInt(DataConstants.CURRENT_REGION_ID);
		}
		else {
			worldId = getIntent().getExtras().getInt(EDIT_WORLD_INTENT_WORLD_ID);
		}

		setContentView(R.layout.edit_world);

		if(getResources().getBoolean(R.bool.has_two_panes)) {
			propsFragment = (EditWorldPropsFragment) getFragmentManager().findFragmentById(
					R.id.props_fragment);
			regionFragment = (EditWorldRegionFragment) getFragmentManager().findFragmentById(
					R.id.region_editor_fragment);
		}
		else {
			propsFragment = new EditWorldPropsFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.worldEditorFragmentContainer, propsFragment)
					.addToBackStack(null)
					.commit();
			regionFragment = null;
		}

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(worldId != DataConstants.UNINITIALIZED) {
			eventBus.post(new WorldEvent.LoadById(worldId));
		}
		if(selectedRegionId != DataConstants.UNINITIALIZED) {
			eventBus.post(new RegionEvent.LoadById(selectedRegionId));
		}
	}

//	@Override
//	protected void onStart() {
//		super.onStart();
//		if(propsFragment != null) {
//			propsFragment.loadWorld(worldId);
//		}
//		if(regionFragment != null) {
//			regionFragment.setRegion(worldId, selectedRegionId);
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();
		if(eventBus != null && !eventBus.isRegistered(this)) {
			eventBus.register(this);
		}
	}

	@Override
	protected void onPause() {
		if(eventBus != null && eventBus.isRegistered(this)) {
			eventBus.unregister(this);
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_world_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		return id == R.id.actionSettings || super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(DataConstants.CURRENT_WORLD_ID, worldId);
		outState.putInt(DataConstants.CURRENT_REGION_ID, selectedRegionId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	// </editor-fold>

	// <editor-fold> desc="EventBus event handler methods">
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSelected(RegionEvent.Selected event) {
		selectedRegionId = event.getRegion().getId();
		if(event.isSwitchFragments()) {
			if (!getResources().getBoolean(R.bool.has_two_panes)) {
				regionFragment = new EditWorldRegionFragment();
				getFragmentManager().beginTransaction()
						.replace(R.id.worldEditorFragmentContainer, regionFragment)
						.addToBackStack(null)
						.commit();
				propsFragment = null;
			}
			regionFragment.setRegion(event.getRegion());
		}
	}
	// </editor-fold>

	// <editor-fold desc="Getters and setters">
	public int getWorldId() {
		return worldId;
	}
	public ActivityComponent getActivityComponent() {
		return activityComponent;
	}
	// </editor-fold>
}
