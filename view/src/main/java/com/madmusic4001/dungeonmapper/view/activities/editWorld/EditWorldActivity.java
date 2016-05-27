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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.RegionEventHandler;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionSelectedEvent;
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
	private static final String ACTIVE_WORLD_ID_KEY = "saved_world_id";
	private static final String ACTIVE_REGION_ID_KEY = "saved_region_id";

	@Inject
	protected EventBus              eventBus;
	@Inject
	protected RegionEventHandler    regionEventHandler;
	private ActivityComponent		activityComponent;
	private EditWorldPropsFragment	propsFragment;
	private EditWorldRegionFragment regionFragment;
	private int                     worldId = DataConstants.UNINITIALIZED;
	private int  					selectedRegionId = DataConstants.UNINITIALIZED;

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
			worldId = savedInstanceState.getInt(ACTIVE_WORLD_ID_KEY);
			selectedRegionId = savedInstanceState.getInt(ACTIVE_REGION_ID_KEY);
		}
		else {
			worldId = getIntent().getExtras().getInt(EDIT_WORLD_INTENT_WORLD_ID);
			Log.e("EditWorldActivity", "Activity starting with world ID " + worldId);
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(propsFragment != null) {
			Log.d(this.getClass().getName(), "Calling propsFragment.loadWorld(\"" + worldId + "\")");
			propsFragment.loadWorld(worldId);
		}
		if(regionFragment != null) {
			Log.d(this.getClass().getName(), "Calling regionFragment.setRegion(\"" + worldId + "\", \"" +
					selectedRegionId + "\")");
			regionFragment.setRegion(worldId, selectedRegionId);
		}
	}

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

		outState.putInt(ACTIVE_WORLD_ID_KEY, worldId);
		outState.putInt(ACTIVE_REGION_ID_KEY, selectedRegionId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	// </editor-fold>

	// <editor-fold> desc="EventBus event handler methods">
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSelected(RegionSelectedEvent event) {
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
			regionFragment.setRegion(worldId, selectedRegionId);
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
