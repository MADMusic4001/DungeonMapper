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
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.CellExitTypeEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.TerrainEventHandler;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldEvent;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.RegionRxHandler;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.WorldRxHandler;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;
import com.madmusic4001.dungeonmapper.view.di.components.ActivityComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.madmusic4001.dungeonmapper.view.utils.IntentConstants.EDIT_WORLD_INTENT_WORLD_ID;

/**
 *
 */
public class EditWorldActivity extends Activity {
	private static final String PROPERTIES_FRAGMENT_TAG = "properties_fragment";
	private static final String REGION_FRAGMENT_TAG = "region_fragment";
	@Inject
	protected FilterCreator            filterCreator;
	@Inject
	protected WorldRxHandler           worldRxHandler;
	@Inject
	protected RegionRxHandler          regionRxHandler;
	@Inject
	protected CellExitTypeEventHandler cellExitTypeEventHandler;
	@Inject
	protected TerrainEventHandler      terrainEventHandler;
	private   ActivityComponent        activityComponent;
	private   EditWorldPropsFragment   propsFragment;
	private   EditWorldRegionFragment  regionFragment;
	private int							worldId = DataConstants.UNINITIALIZED;
	private int							selectedRegionId = DataConstants.UNINITIALIZED;
//	private Collection<Region>

	// <editor-fold desc="Activity lifecycle event handlers">
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreate");
		super.onCreate(savedInstanceState);

		activityComponent = ((DungeonMapperApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);

		setContentView(R.layout.edit_world);

		FragmentManager fragmentManager = getFragmentManager();

		if (savedInstanceState != null) {
			worldId = savedInstanceState.getInt(DataConstants.CURRENT_WORLD_ID);
			selectedRegionId = savedInstanceState.getInt(DataConstants.CURRENT_REGION_ID);
			if(!getResources().getBoolean(R.bool.has_two_panes)) {
				propsFragment = (EditWorldPropsFragment) fragmentManager.findFragmentByTag(PROPERTIES_FRAGMENT_TAG);
				regionFragment = (EditWorldRegionFragment) fragmentManager.findFragmentByTag(REGION_FRAGMENT_TAG);
			}
		}
		else {
			if(!getResources().getBoolean(R.bool.has_two_panes)) {
				propsFragment = new EditWorldPropsFragment();
				getFragmentManager().beginTransaction()
						.replace(R.id.worldEditorFragmentContainer, propsFragment, PROPERTIES_FRAGMENT_TAG)
						.commit();
				regionFragment = null;
			}
			worldId = getIntent().getExtras().getInt(EDIT_WORLD_INTENT_WORLD_ID);
			selectedRegionId = DataConstants.UNINITIALIZED;
		}

		if(getResources().getBoolean(R.bool.has_two_panes)) {
			propsFragment = (EditWorldPropsFragment)fragmentManager.findFragmentById(R.id.props_fragment);
			regionFragment = (EditWorldRegionFragment)fragmentManager.findFragmentById(R.id.region_editor_fragment);
		}

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(propsFragment != null && worldId != DataConstants.UNINITIALIZED) {
			worldRxHandler.getWorld(worldId)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<World>() {
						@Override
						public void onCompleted() {

						}

						@Override
						public void onError(Throwable e) {

						}

						@Override
						public void onNext(World world) {
							if(propsFragment != null) {
								propsFragment.setWorld(world);
							}
						}
					});
		}
		if(regionFragment != null && selectedRegionId != DataConstants.UNINITIALIZED) {
			regionFragment.loadRegion(selectedRegionId);
		}
		Toast.makeText(this, getString(R.string.toast_loading_regions), Toast.LENGTH_LONG).show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_world_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onOptionsItemSelected");
		int id = item.getItemId();
		if(id == android.R.id.home) {
			if (!getResources().getBoolean(R.bool.has_two_panes) && regionFragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
				propsFragment = (EditWorldPropsFragment) fragmentManager.findFragmentByTag(PROPERTIES_FRAGMENT_TAG);
				worldRxHandler.getWorld(worldId)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<World>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onNext(World world) {
								if(propsFragment != null) {
									propsFragment.setWorld(world);
								}
							}
						});
				regionFragment = null;
				return true;
			}
			else {
				NavUtils.navigateUpFromSameTask(this);
			}
		}
		return id == R.id.actionSettings || super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		FragmentManager fm = getFragmentManager();
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
						.replace(R.id.worldEditorFragmentContainer, regionFragment, REGION_FRAGMENT_TAG)
						.addToBackStack(null)
						.commit();
				propsFragment = null;
			}
			regionFragment.setRegion(event.getRegion());
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldLoaded(WorldEvent.SingleLoaded event) {
		if(event.isSuccessful() && propsFragment != null) {
			propsFragment.setWorld(event.getWorld());
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSaved(RegionEvent.Saved event) {
		if(event.isSuccessful() ) {
			if (!getResources().getBoolean(R.bool.has_two_panes) && regionFragment == null) {
				regionFragment = new EditWorldRegionFragment();
				getFragmentManager().beginTransaction()
							.replace(R.id.worldEditorFragmentContainer, regionFragment, REGION_FRAGMENT_TAG)
							.addToBackStack(null)
							.commit();
				propsFragment = null;
				regionFragment.setRegion(event.getItem());
			}
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
