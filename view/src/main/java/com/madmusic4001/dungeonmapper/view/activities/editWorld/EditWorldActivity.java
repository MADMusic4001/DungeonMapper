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
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;
import com.madmusic4001.dungeonmapper.view.di.components.ActivityComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;

import static com.madmusic4001.dungeonmapper.view.utils.IntentConstants.EDIT_WORLD_INTENT_WORLD_NAME;

/**
 *
 */
public class EditWorldActivity extends Activity
		implements EditWorldPropsFragment.OnEditWorldPropsEventsListener,
		EditWorldRegionFragment.EditWorldRegionEventsListener {
	private static final String ACTIVE_WORLD_NAME_KEY  = "saved_name";
	private static final String ACTIVE_REGION_NAME_KEY = "saved_region";

	private ActivityComponent		activityComponent;
	private EditWorldPropsFragment	propsFragment;
	private EditWorldRegionFragment regionFragment;
	private String					worldName = null;
	private String					selectedRegionName = null;

	// <editor-fold> desc="EditWorldPropsFragment.OnEditWorldPropsEventsListener interface implementation">
	@Override
	public void onRegionSelected(Region region, boolean switchFragments) {
		selectedRegionName = region.getName();
		if(switchFragments) {
			if (!getResources().getBoolean(R.bool.has_two_panes)) {
				regionFragment = new EditWorldRegionFragment();
				getFragmentManager().beginTransaction()
						.replace(R.id.worldEditorFragmentContainer, regionFragment)
						.addToBackStack(null)
						.commit();
				propsFragment = null;
			}
			else {
				regionFragment.loadRegion(worldName, selectedRegionName);
			}
		}
	}
	// </editor-fold>

	// <editor-fold desc="EditWorldRegionFragment.EditWorldRegionEventsListener interface implementation">
	@Override
	public String getRegionName() {
		return selectedRegionName;
	}

	@Override
	public void regionNameChanged() {
		if(propsFragment != null) {
			propsFragment.updateRegionsList();
		}
	}

	@Override
	public String getWorldName() {
		return worldName;
	}
	// </editor-fold>

	// <editor-fold desc="Activity lifecycle event handlers">
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreate");
		super.onCreate(savedInstanceState);

		activityComponent = ((DungeonMapperApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);
		Log.d("Lifecycle", "activityComponent created");

		if (savedInstanceState != null) {
			worldName = savedInstanceState.getString(ACTIVE_WORLD_NAME_KEY);
			selectedRegionName = savedInstanceState.getString(ACTIVE_REGION_NAME_KEY);
		}
		else {
			worldName = getIntent().getExtras().getString(EDIT_WORLD_INTENT_WORLD_NAME);
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
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onStart");
		super.onStart();
		if(propsFragment != null) {
			Log.d(this.getClass().getName(), "Calling propsFragment.loadWorld(\"" + worldName + "\")");
			propsFragment.loadWorld(worldName);
		}
		if(regionFragment != null) {
			Log.d(this.getClass().getName(), "Calling regionFragment.loadRegion(\"" +  worldName + "\", \"" +
					selectedRegionName + "\")");
			regionFragment.loadRegion(worldName, selectedRegionName);
		}
	}

	@Override
	protected void onResume() {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onResume");
		super.onResume();
		Debug.stopMethodTracing();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreateOptionsMenu");
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
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onSaveInstanceState");
		super.onSaveInstanceState(outState);

		outState.putString(ACTIVE_WORLD_NAME_KEY, worldName);
		outState.putString(ACTIVE_REGION_NAME_KEY, selectedRegionName);
	}

	@Override
	protected void onDestroy() {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onDestroy");
		super.onDestroy();
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">

	public ActivityComponent getActivityComponent() {
		return activityComponent;
	}
	// </editor-fold>
}
