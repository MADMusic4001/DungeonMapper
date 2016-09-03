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
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.RegionRxHandler;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.WorldRxHandler;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.RegionDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.ComparatorUtils;
import com.madmusic4001.dungeonmapper.view.adapters.RegionListAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;

/**
 * Manages the world properties editor user interface.
 */
public class EditWorldPropsFragment extends Fragment {
	private static final String LOG_TAG = "EditWorldPropsFragment";
	@Inject
	protected RegionListAdapter regionListAdapter;
	@Inject
	protected FilterCreator     filterCreator;
	@Inject
	protected ComparatorUtils   comparatorUtils;
	@Inject
	protected WorldRxHandler    worldRxHandler;
	@Inject
	protected RegionRxHandler   regionRxHandler;
	private   TextView          worldNameView;
	private   CheckBox          zeroBasedCoordinatesView;
	private   Spinner           originView;
	private   TextView          regionWidthView;
	private   TextView          regionHeightView;
	private   ListView          regionsListView;
	private   World             world;
	private   boolean           initialized = false;

	// <editor-fold desc="Public API methods">

	/**
	 * Adds a Region instance to the ListView
	 *
	 * @param region  the Reion instance to add to the ListView
	 */
	public void addRegionToList(Region region) {
		int position = regionListAdapter.getPosition(region);
		if(position <= 0) {
			regionListAdapter.add(region);
		}
		else {
			LinearLayout v = (LinearLayout) regionsListView.getChildAt(position - regionsListView.getFirstVisiblePosition());
			if (v != null) {
				TextView textView = (TextView) v.findViewById(R.id.nameHeader);
				textView.setText(region.getName());
				textView = (TextView) v.findViewById(R.id.modifiedHeader);
				textView.setText(getFormattedDateOrTime(region.getModifiedTs().getTimeInMillis()));
			}
		}
		regionListAdapter.notifyDataSetChanged();
	}

	/**
	 * Removes a Region instance from the ListView
	 *
	 * @param region  the Region instance to remove from the ListView
	 */
	public void removeRegionFromList(Region region) {
		regionListAdapter.remove(region);
		regionListAdapter.notifyDataSetChanged();
	}

	/**
	 * Set the World instance to be displayed
	 *
	 * @param world  the World instance to display
	 */
	public void setWorld(@NonNull World world) {
		this.world = world;
		if(initialized) {
			worldNameView.setText(world.getName());
			zeroBasedCoordinatesView.setChecked(world.getOriginOffset() == 0);
			originView.setSelection(world.getOriginLocation());
			regionWidthView.setText(String.valueOf(world.getRegionWidth()));
			regionHeightView.setText(String.valueOf(world.getRegionHeight()));

			regionListAdapter.clear();
			regionListAdapter.addAll(world.getRegionNameMap().values());
			regionListAdapter.notifyDataSetChanged();
		}
	}
	// </editor-fold>

	// <editor-fold desc="Fragment lifecycle event handlers">
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
							 Bundle savedInstanceState) {
		((EditWorldActivity)getActivity()).getActivityComponent().
				newFragmentComponent(new FragmentModule(this)).injectInto(this);

		View layout = inflater.inflate(R.layout.edit_world_fragment, container, false);

		initNameView(layout);
		initZeroBasedCoordinatesView(layout);
		initOriginView(layout);
		initRegionWidthView(layout);
		initRegionHeightView(layout);
		initRegionListView(layout);

		initialized = true;
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(getResources().getBoolean(R.bool.has_two_panes)) {
			regionsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
				Calendar currentTime = Calendar.getInstance();
				region.setCreateTs(currentTime);
				region.setModifiedTs(currentTime);
				region.setHeight(world.getRegionHeight());
				region.setWidth(world.getRegionWidth());
				regionRxHandler.saveRegion(region)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Region>() {
							@Override
							public void onCompleted() {}
							@Override
							public void onError(Throwable e) {
								Log.e(LOG_TAG, "Exception caught saving new Region.", e);
							}
							@Override
							public void onNext(Region region) {
								if(regionListAdapter.getPosition(region) <= 0) {
									regionListAdapter.add(region);
								}
								regionListAdapter.notifyDataSetChanged();
							}
						});
			}
			result = true;
		}
		return result || super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
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
					((EditWorldActivity)getActivity()).editRegion(region);
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
					regionRxHandler.deleteRegions(filters)
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(new Subscriber<Collection<Region>>() {
								@Override
								public void onCompleted() {}
								@Override
								public void onError(Throwable e) {
									Log.e(LOG_TAG, "Exception caught deleting region", e);
									Toast.makeText(getActivity(), getString(R.string.toast_worlds_deleted_error),
											Toast.LENGTH_SHORT).show();
								}
								@Override
								public void onNext(Collection<Region> regions) {
									for (Region deletedRegion : regions) {
										regionListAdapter.remove(deletedRegion);
									}
									regionListAdapter.notifyDataSetChanged();
									Toast.makeText(getActivity(), String.format(getString(R.string.toast_worlds_deleted), regions.size()),
											Toast.LENGTH_SHORT).show();
								}
							});
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

	// <editor-fold desc="Getters and setters">
	public World getWorld() {
		return world;
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">
	private void initNameView(View layout) {
		worldNameView = (TextView)layout.findViewById(R.id.worldNameEdit);
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
						saveWorld();
					}
				}
			}
		});
	}

	private void initZeroBasedCoordinatesView(View layout) {
		zeroBasedCoordinatesView = (CheckBox) layout.findViewById(R.id.zeroBasedCoordinatesCb);
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
							saveWorld();
						}
					}
				}
		);
	}

	private void initOriginView(View layout) {
		originView = (Spinner) layout.findViewById(R.id.originLocSpinner);
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
					saveWorld();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initRegionWidthView(View layout) {
		regionWidthView = (TextView) layout.findViewById(R.id.mapWidthEdit);
		if(world != null) {
			regionWidthView.setText(String.valueOf(world.getRegionWidth()));
		}

		regionWidthView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
		regionWidthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					int newWidth = Integer.valueOf(regionWidthView.getText().toString());
					if (world != null && world.getRegionWidth() != newWidth) {
						world.setRegionWidth(newWidth);
						for(Region aRegion : world.getRegionNameMap().values()) {
							aRegion.setWidth(newWidth);
						}
						saveWorld();
					}
				}
			}
		});
	}

	private void initRegionHeightView(View layout) {
		regionHeightView = (TextView) layout.findViewById(R.id.mapHeightEdit);
		if(world != null) {
			regionHeightView.setText(String.valueOf(world.getRegionHeight()));
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
		regionHeightView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					int newHeight = Integer.valueOf(regionHeightView.getText().toString());
					if (world != null && world.getRegionHeight() != newHeight) {
						world.setRegionHeight(newHeight);
						for(Region aRegion : world.getRegionNameMap().values()) {
							aRegion.setHeight(newHeight);
						}
						saveWorld();
						//TODO: Update region fragment with new height
					}
				}
			}
		});
	}

	private void initRegionListView(View layout) {
		regionsListView = (ListView) layout.findViewById(R.id.regionList);
		regionListAdapter = new RegionListAdapter(getActivity());

		// Create and set onClick methods for sorting the Region list.
		layout.findViewById(R.id.nameHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionNameComparator());
			}
		});
		layout.findViewById(R.id.createdHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionCreateTsComparator());
			}
		});
		layout.findViewById(R.id.modifiedHeader).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				regionListAdapter.sort(comparatorUtils.getRegionModifiedTsCompartor());
			}
		});

		regionsListView.setAdapter(regionListAdapter);

		if(world != null) {
			regionListAdapter.clear();
			regionListAdapter.addAll(world.getRegionNameMap().values());
			regionListAdapter.notifyDataSetChanged();
		}

		regionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Region region = (Region) regionsListView.getItemAtPosition(position);
				((EditWorldActivity)getActivity()).showRegion(region);
			}
		});
		registerForContextMenu(regionsListView);
	}

	private void saveWorld() {
		worldRxHandler.saveWorld(world)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<World>() {
					@Override
					public void onCompleted() {}
					@Override
					public void onError(Throwable e) {
						Log.e(LOG_TAG, "Exception caught saving World instance.", e);
						Toast.makeText(getActivity(), getString(R.string.toast_world_save_error), Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNext(World world) {
						Toast.makeText(getActivity(), getString(R.string.toast_world_saved), Toast.LENGTH_SHORT).show();
					}
				});
	}

	private String getFormattedDateOrTime(long timeInMillis) {
		String result;

		Calendar modDateTime = Calendar.getInstance();
		modDateTime.setTimeInMillis(timeInMillis);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if (modDateTime.after(today)) {
			result = DateFormat.getTimeFormat(getActivity()).format(modDateTime.getTime());
		}
		else {
			result = DateFormat.getDateFormat(getActivity()).format(modDateTime.getTime());
		}
		return result;
	}
	// </editor-fold>
}
