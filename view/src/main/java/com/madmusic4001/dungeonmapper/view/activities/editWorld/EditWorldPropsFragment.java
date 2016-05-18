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

import android.app.Activity;
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
import com.madmusic4001.dungeonmapper.controller.EditWorldPropsController;
import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.view.adapters.RegionListAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;

import java.util.Collection;
import java.util.Comparator;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;

/**
 * Manages the world properties editor user interface.
 *
 * @author Mark Danley
 * Created 8/4/2014
 */
public class EditWorldPropsFragment extends Fragment implements
		EditWorldPropsController.EditWorldPropsUpdateHandler {
	@Inject
	protected EditWorldPropsController		controller;
	@Inject
	protected RegionListAdapter				regionListAdapter;
	@Inject
	WorldManager							worldManager;
	private OnEditWorldPropsEventsListener	callbacksImpl = null;
	private TextView						worldNameView;
	private CheckBox						zeroBasedCoordinatesView;
	private Spinner							originView;
	private TextView						regionWidthView;
	private TextView						regionHeightView;
	private ListView						regionsListView;
	private World							world;
	private EditWorldActivity				activity;

	// <editor-fold desc="Fragment lifecycle event handlers">
	@Override
	public void onAttach(Activity activity) {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onAttach");
		super.onAttach(activity);

		this.activity = (EditWorldActivity)activity;
		try {
			callbacksImpl = (OnEditWorldPropsEventsListener)activity;
		}
		catch (ClassCastException ex){
			throw new ClassCastException(activity.toString()
												 + " must implement OnEditWorldPropsEventsListener");
		}
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
		Log.d(this.getClass().getName(), "Component = " + ((EditWorldActivity)activity).getActivityComponent());
//		((EditWorldActivity)activity).getActivityComponent().
//				newFragmentComponent(new FragmentModule(this)).injectInto(this);

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
//				controller.createRegion(world, null);
			}
			result = true;
		}
		return result || super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
		Log.d("Lifecycle", this.getClass().getSimpleName() +  ".onDetach");
		super.onDetach();
		this.callbacksImpl = null;
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
					callbacksImpl.onRegionSelected(region, true);
					return true;
				}
				else {
					return false;
				}
			case R.id.region_item_delete:
				region = (Region)regionsListView.getItemAtPosition(info.position);
				if(region != null) {
//					controller.deleteRegion(region);
					return true;
				}
				else {
					return false;
				}
			default:
				return super.onContextItemSelected(item);
		}
	}
	// </editor-fold>

	// <editor-fold desc="Public action methods">
	public void updateRegionsList() {
		regionListAdapter.clear();
//		regionListAdapter.addAll(worldManager.getRegionsForWorld(world));
		regionListAdapter.notifyDataSetChanged();
	}

	public void loadWorld(String worldName) {
//		controller.loadWorld(worldName);
	}
	// </editor-fold>

	// <editor-fold desc="EditWorldPropsController.EditWorldPropsUpdateHandle interface implementation methods">
	@Override
	public void onWorldLoaded(World world) {
		this.world = world;

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
			regionWidthView.setText(Integer.toString(world.getRegionWidth()));
		}
		if(regionHeightView != null) {
			regionHeightView.setText(Integer.toString(world.getRegionHeight()));
		}

		Toast.makeText(getActivity(), getString(R.string.edit_world_props_loading_regions_toast), Toast.LENGTH_LONG).show();
//		controller.loadRegionsForWorld(world);
	}

	@Override
	public void onWorldSaved(World world) {
		Toast.makeText(getActivity(), getString(R.string.edit_world_props_world_saved_toast), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRegionsLoaded(Collection<Region> regions) {
		if(regionListAdapter != null) {
			regionListAdapter.clear();
			regionListAdapter.addAll(regions);
			regionListAdapter.notifyDataSetChanged();
		}
		Toast.makeText(getActivity(), getString(R.string.edit_world_props_regions_loaded_toast), Toast.LENGTH_SHORT).show();
		if(regions.size() > 0) {
			callbacksImpl.onRegionSelected((Region) regions.toArray()[0], false);
		}
	}

	@Override
	public void onSortRegionList(Comparator<Region> comparator) {
		regionListAdapter.sort(comparator);
	}

	@Override
	public void onRegionCreated(Region newRegion, Collection<Region> regions) {
		regionListAdapter.notifyDataSetChanged();
		regionListAdapter.clear();
		regionListAdapter.addAll(regions);
		regionListAdapter.notifyDataSetChanged();
		callbacksImpl.onRegionSelected(newRegion, true);
	}

	@Override
	public void onRegionDeleted(Collection<Region> regions) {
		regionListAdapter.notifyDataSetChanged();
		regionListAdapter.clear();
		regionListAdapter.addAll(regions);
		regionListAdapter.notifyDataSetChanged();
		if(regions.size() > 0) {
			callbacksImpl.onRegionSelected((Region) regions.toArray()[0], false);
		}
	}
	// </editor-fold>

	// <editor-fold desc="Getters and setters">
	public World getWorld() {
		return world;
	}
	// </editor-fold>

	// <editor-fold desc="Interface declarations">
	/**
	 * Defines methods to allow the fragment to communicate with the implementer (EditWorldActivity).
	 */
	public interface OnEditWorldPropsEventsListener {
		/**
		 * Notifies the implementer when a {@code Region} is selected by the user.
		 *
		 * @param region the selected {@link Region}.
		 * @param switchFragment  true if the edit regions fragment should be displayed, false if
		 *                              the world props fragment should remain.
		 */
		void onRegionSelected(Region region, boolean switchFragment);
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
//						controller.saveWorld(world);
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
//							controller.saveWorld(world);
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
//					controller.saveWorld(world);
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
//					controller.saveWorld(world);
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
//						controller.saveWorld(world);
						Log.d(((Object) this).getClass().getName(), "Executed "
								+ "EditWorldPropsController#saveWorld(World world)");
						//TODO: Update region fragment with new width
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
//					controller.saveWorld(world);
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
//						controller.saveWorld(world);
						Log.d(((Object) this).getClass().getName(), "Executed "
								+ "EditWorldPropsController#saveWorld(World world)");
						//TODO: Update region fragment with new height
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

		// Create and set onClick methods for sorting the {@link World} list.
		headerView.findViewById(R.id.nameHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				controller.sortRegionsByName();
			}
		});
		headerView.findViewById(R.id.createdHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				controller.sortRegionsByCreatedTs();
			}
		});
		headerView.findViewById(R.id.modifiedHeader).setOnClickListener(new View.OnClickListener
				() {
			@Override
			public void onClick(View v) {
//				controller.sortRegionsByModifiedTs();
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
				callbacksImpl.onRegionSelected(region, true);
			}
		});
		registerForContextMenu(regionsListView);
	}
	// </editor-fold>
}
