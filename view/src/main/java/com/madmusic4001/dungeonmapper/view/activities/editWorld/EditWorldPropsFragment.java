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

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionEvent;
import com.madmusic4001.dungeonmapper.controller.events.world.WorldEvent;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.RegionDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.WorldDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.ComparatorUtils;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.adapters.RegionListAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;

/**
 * Manages the world properties editor user interface.
 */
public class EditWorldPropsFragment extends Fragment {
	@Inject
	protected RegionListAdapter				regionListAdapter;
	@Inject
	protected EventBus                      eventBus;
	@Inject
	protected FilterCreator                 filterCreator;
	@Inject
	protected ComparatorUtils               comparatorUtils;
	private TextView						worldNameView;
	private CheckBox						zeroBasedCoordinatesView;
	private Spinner							originView;
	private TextView						regionWidthView;
	private TextView						regionHeightView;
	private ListView						regionsListView;
	private World							world;

	// <editor-fold desc="Fragment lifecycle event handlers">

	@Override
	public void onResume() {
		super.onResume();
		if(eventBus != null && !eventBus.isRegistered(this)) {
			eventBus.register(this);
		}
	}

	@Override
	public void onPause() {
		if(eventBus != null) {
			eventBus.unregister(this);
		}
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreate");
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreateView");
		Log.d(this.getClass().getName(), "Component = " + ((EditWorldActivity)getActivity()).getActivityComponent());
		((EditWorldActivity)getActivity()).getActivityComponent().
				newFragmentComponent(new FragmentModule(this)).injectInto(this);
		if(eventBus != null && !eventBus.isRegistered(this)) {
			eventBus.register(this);
		}

		if(savedInstanceState != null) {
			int worldId = savedInstanceState.getInt(DataConstants.CURRENT_WORLD_ID, DataConstants.UNINITIALIZED);
			if(worldId != DataConstants.UNINITIALIZED) {
				eventBus.post(new WorldEvent.LoadById(worldId));
			}
		}
		View layout = inflater.inflate(R.layout.edit_world_fragment, container, false);

		worldNameView = (TextView)layout.findViewById(R.id.worldNameEdit);
		initNameView();

		zeroBasedCoordinatesView = (CheckBox) layout.findViewById(R.id.zeroBasedCoordinatesCb);
		initZeroBasedCoordinatesView();

		originView = (Spinner) layout.findViewById(R.id.originLocSpinner);
		initOriginView();

		regionWidthView = (TextView) layout.findViewById(R.id.mapWidthEdit);
		initRegionWidthView();

		regionHeightView = (TextView) layout.findViewById(R.id.mapHeightEdit);
		initRegionHeightView();

		regionsListView = (ListView) layout.findViewById(R.id.mapList);
		initRegionListView();

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		if(getResources().getBoolean(R.bool.has_two_panes)) {
			regionsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_world_props_fragment_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;

		int id = item.getItemId();
		if (id == R.id.actionNewRegion) {
			if(world != null) {
				Region region = new Region(getString(R.string.defaultRegionName), world);
				eventBus.post(new RegionEvent.Save(region));
			}
			result = true;
		}
		return result || super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onDetach");
		super.onDetach();
		this.world = null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.region_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Region region;

		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

		switch (item.getItemId()) {
			case R.id.region_item_edit:
				region = (Region)regionsListView.getItemAtPosition(info.position);
				if(region != null) {
					eventBus.post(new RegionEvent.Selected(region, true));
					return true;
				}
				else {
					return false;
				}
			case R.id.region_item_delete:
				region = (Region)regionsListView.getItemAtPosition(info.position);
				if(region != null) {
					Collection<DaoFilter> filters = new ArrayList<>(1);
					filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
							RegionDaoSqlImpl.RegionsContract._ID,
							String.valueOf(region.getId())));
					eventBus.post(new RegionEvent.Delete(filters));
					return true;
				}
				else {
					return false;
				}
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(world != null) {
			outState.putInt(DataConstants.CURRENT_WORLD_ID, world.getId());
		}
	}

	// </editor-fold>

	// <editor-fold desc="Public action methods">
	public void loadWorld(int worldId) {
		Collection<DaoFilter> filters = new ArrayList<>(1);
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
												  WorldDaoSqlImpl.WorldsContract._ID,
												  String.valueOf(worldId)));
		eventBus.post(new WorldEvent.Load(filters));
	}
	// </editor-fold>

	// <editor-fold desc="EventBus subscriber methods">
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldsLoaded(WorldEvent.Loaded event) {
		this.world = event.getItems().iterator().next();

		if(worldNameView != null) {
			worldNameView.setText(world.getName());
		}
		if(zeroBasedCoordinatesView != null) {
			zeroBasedCoordinatesView.setChecked(world.getOriginOffset() == 0);
		}
		if(originView != null) {
			originView.setSelection(world.getOriginLocation());
		}
		if(regionWidthView != null) {
			regionWidthView.setText(String.valueOf(world.getRegionWidth()));
		}
		if(regionHeightView != null) {
			regionHeightView.setText(String.valueOf(world.getRegionHeight()));
		}

		Toast.makeText(getActivity(), getString(R.string.toast_loading_regions), Toast.LENGTH_LONG).show();
		if(world != null && world.getId() != DataConstants.UNINITIALIZED) {
			Collection<DaoFilter> filters = new ArrayList<>(1);
			filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
													  RegionDaoSqlImpl.RegionsContract.WORLD_ID_COLUMN_NAME,
													  String.valueOf(world.getId())));
			eventBus.post(new RegionEvent.Load(filters));
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onWorldSaved(WorldEvent.Saved event) {
		String toastString;

		if(event.isSuccessful()) {
			toastString = getString(R.string.toast_world_saved);
		}
		else {
			toastString = getString(R.string.toast_world_save_error);
		}
		Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionsLoaded(RegionEvent.Loaded event) {
		String toastString;

		if(event.isSuccessful()) {
			toastString = String.format(getString(R.string.toast_regions_loaded), event.getItems().size());
			if(regionListAdapter != null) {
				regionListAdapter.addAll(event.getItems());
				regionListAdapter.notifyDataSetChanged();
			}
		}
		else {
			toastString = getString(R.string.toast_regions_load_error);
			if(regionListAdapter != null) {
				regionListAdapter.clear();
				regionListAdapter.notifyDataSetInvalidated();
			}
		}
		Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();
		if(event.getItems().size() > 0) {
			eventBus.post(new RegionEvent.Selected((Region) event.getItems().toArray()[0], false));
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSaved(RegionEvent.Saved event) {
		if(event.isSuccessful()) {
			if(regionListAdapter.getPosition(event.getItem()) <= 0) {
				regionListAdapter.add(event.getItem());
			}
			regionListAdapter.notifyDataSetChanged();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionsDeleted(RegionEvent.Deleted event) {
		String toastString;

		if(event.isSuccessful()) {
			for (Region deletedRegion : event.getDeleted()) {
				regionListAdapter.remove(deletedRegion);
			}
			if (regionListAdapter.getCount() > 0) {
				regionListAdapter.notifyDataSetChanged();
			}
			else {
				regionListAdapter.notifyDataSetInvalidated();
			}
			toastString = String.format(getString(R.string.toast_worlds_deleted), event.getDeleted().size());
		}
		else {
			toastString = getString(R.string.toast_worlds_deleted_error);
		}
		Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();
	}
	// </editor-fold>

	// <editor-fold desc="Getters and setters">
	public World getWorld() {
		return world;
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">
	private void initNameView() {
		if(world != null) {
			worldNameView.setText(world.getName());
		}

		worldNameView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					worldNameView.setError(getString(R.string.validation_worldNameRequired));
				}
			}
		});
		worldNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String newName = worldNameView.getText().toString();
					if (world != null && !world.getName().equals(newName)) {
						world.setName(newName);
						eventBus.post(new WorldEvent.Save(world));
					}
				}
			}
		});
	}

	private void initZeroBasedCoordinatesView() {
		if(world != null) {
			zeroBasedCoordinatesView.setChecked(world.getOriginOffset() == 0);
		}

		zeroBasedCoordinatesView.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						int newOffset = buttonView.isChecked() ? 0 : 1;
						if (world != null && world.getOriginOffset() != newOffset) {
							world.setOriginOffset(newOffset);
							eventBus.post(new WorldEvent.Save(world));
							Log.d(((Object) this).getClass().getName(), "Executed "
									+ "EditWorldPropsController#saveWorld(World world)");
						}
					}
				}
		);
	}

	private void initOriginView() {
		if(world != null) {
			originView.setSelection(world.getOriginLocation());
		}

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(),
				R.array.orientation_names,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		originView.setAdapter(adapter);
		originView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				@OriginLocation int newPosition = (int) parent.getSelectedItemId();
				if (world != null && world.getOriginLocation() != newPosition) {
					world.setOriginLocation(newPosition);
					eventBus.post(new WorldEvent.Save(world));
					Log.d(((Object) this).getClass().getName(), "Executed "
							+ "EditWorldPropsController#saveWorld(World world)");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initRegionWidthView() {
		if(world != null) {
			regionWidthView.setText(Integer.toString(world.getRegionWidth()));
		}

		regionWidthView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					int value = Integer.parseInt(s.toString());
					if (value < 1) {
						regionWidthView.setError(getString(R.string.validation_widthTooSmall));
					}
					if (value > World.MAX_REGION_WIDTH) {
						regionWidthView.setError(String.format(
								getString(R.string.validation_widthTooLarge),
								World.MAX_REGION_WIDTH));
					}
				}
				catch (NumberFormatException ignored) {
					// android:inputType is set to number so parseInt will never throw this
				}
			}
		});
		regionWidthView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int newWidth = Integer.valueOf(v.getText().toString());
				if (world != null && world.getRegionWidth() != newWidth) {
					world.setRegionWidth(newWidth);
					eventBus.post(new WorldEvent.Save(world));
					Log.d(((Object) this).getClass().getName(), "Executed "
							+ "EditWorldPropsController#saveWorld(World world)");
					//TODO: Update region fragment with new width
				}
				return true;
			}
		});
		regionWidthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					int newWidth = Integer.valueOf(regionWidthView.getText().toString());
					if (world != null && world.getRegionWidth() != newWidth) {
						world.setRegionWidth(newWidth);
						eventBus.post(new WorldEvent.Save(world));
						for(Region aRegion : world.getRegionNameMap().values()) {
							aRegion.setWidth(newWidth);
						}
						eventBus.post(new RegionEvent.SizeChanged(world.getRegionHeight(), newWidth));
						Log.d(((Object) this).getClass().getName(), "Executed "
								+ "EditWorldPropsController#saveWorld(World world)");
					}
				}
			}
		});
	}

	private void initRegionHeightView() {
		if(world != null) {
			regionHeightView.setText(Integer.toString(world.getRegionHeight()));
		}

		regionHeightView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				try {
					int value = Integer.parseInt(s.toString());
					if (value < 1) {
						regionHeightView.setError(getString(R.string.validation_heightTooSmall));
					}
					if (value > World.MAX_REGION_HEIGHT) {
						regionHeightView.setError(String.format(
								getString(R.string.validation_heightTooLarge),
								World.MAX_REGION_HEIGHT));
					}
				}
				catch (NumberFormatException ex) {
					regionHeightView.setError(getActivity().getResources().getString(
							R.string.label_regionHeight));
				}
			}
		});
		regionHeightView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int newHeight = Integer.valueOf(v.getText().toString());
				if (world != null && world.getRegionHeight() != newHeight) {
					world.setRegionHeight(newHeight);
					eventBus.post(new WorldEvent.Save(world));
					Log.d(((Object) this).getClass().getName(), "Executed "
							+ "EditWorldPropsController#saveWorld(World world)");
					//TODO: Update region fragment with new height
				}
				return true;
			}
		});
		regionHeightView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					int newHeight = Integer.valueOf(regionHeightView.getText().toString());
					if (world != null && world.getRegionHeight() != newHeight) {
						world.setRegionHeight(newHeight);
						eventBus.post(new WorldEvent.Save(world));
						for(Region aRegion : world.getRegionNameMap().values()) {
							aRegion.setHeight(newHeight);
						}
						eventBus.post(new RegionEvent.SizeChanged(newHeight, world.getRegionWidth()));
						Log.d(((Object) this).getClass().getName(), "Executed "
								+ "EditWorldPropsController#saveWorld(World world)");
					}
				}
			}
		});
	}

	private void initRegionListView() {
		regionListAdapter = new RegionListAdapter(getActivity());
		View headerView = getActivity().getLayoutInflater().inflate(
				R.layout.name_timestamps_header,
				regionsListView,
				false);
		regionsListView.addHeaderView(headerView);

		// Create and set onClick methods for sorting the Region list.
		headerView.findViewById(R.id.nameHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionNameComparator());
			}
		});
		headerView.findViewById(R.id.createdHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionCreateTsComparator());
			}
		});
		headerView.findViewById(R.id.modifiedHeader).setOnClickListener(new View.OnClickListener
				() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionModifiedTsCompartor());
			}
		});

		regionsListView.setAdapter(regionListAdapter);

		if(world != null) {
			regionListAdapter.addAll(world.getRegionNameMap().values());
			regionListAdapter.notifyDataSetChanged();
		}
		regionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Region region = (Region) regionsListView.getItemAtPosition(position);
				eventBus.post(new RegionEvent.Selected(region, true));
			}
		});
		registerForContextMenu(regionsListView);
	}
	// </editor-fold>
}
