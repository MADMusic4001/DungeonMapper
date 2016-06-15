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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypeEvent;
import com.madmusic4001.dungeonmapper.controller.events.region.RegionEvent;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainEvent;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.adapters.CellExitSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.adapters.TerrainSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;
import com.madmusic4001.dungeonmapper.view.views.RegionView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.CURRENT_REGION_ID;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.DOWN;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.EAST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.UP;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WEST;

/**
 *
 */
public class EditWorldRegionFragment extends Fragment {
	@Inject
	protected EventBus                      eventBus;
	@Inject
	protected FilterCreator                 filterCreator;
	private   EditText                      regionNameView;
	private   GridLayout					selectorsGrid;
	private   RegionView                    regionView;
	private   CellExitSpinnerAdapter        upExitAdapter;
	private   CellExitSpinnerAdapter        northExitAdapter;
	private   CellExitSpinnerAdapter        westExitAdapter;
	private   CellExitSpinnerAdapter        eastExitAdapter;
	private   CellExitSpinnerAdapter        southExitAdapter;
	private   CellExitSpinnerAdapter        downExitAdapter;
	private   TerrainSpinnerAdapter         terrainAdapter;
	private   Spinner                       upExitSpinner;
	private   Spinner                       northExitSpinner;
	private   Spinner                       westExitSpinner;
	private   Spinner                       eastExitSpinner;
	private   Spinner                       southExitSpinner;
	private   Spinner                       downExitSpinner;
	private   Spinner                       terrainSpinner;
	private Region  region    = null;
	private boolean showingPalette = true;

	// <editor-fold desc="Fragment lifecycle event handlers">

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.edit_region_fragment, container, false);
		((EditWorldActivity) getActivity()).getActivityComponent().
				newFragmentComponent(new FragmentModule(this)).injectInto(this);
		if(!eventBus.isRegistered(this)) {
			eventBus.register(this);
		}

		if(savedInstanceState != null) {
			int regionId = savedInstanceState.getInt(CURRENT_REGION_ID, DataConstants.UNINITIALIZED);
			if(regionId != DataConstants.UNINITIALIZED) {
				eventBus.post(new RegionEvent.LoadById(regionId));
			}
		}
		initRegionNameView(layout);

		selectorsGrid = (GridLayout)layout.findViewById(R.id.selectorsGrid);

		upExitSpinner = (Spinner) layout.findViewById(R.id.upExitSpinner);
		upExitAdapter = initCellExitSpinner(upExitSpinner, DataConstants.UP);

		northExitSpinner = (Spinner) layout.findViewById(R.id.northExitSpinner);
		northExitAdapter = initCellExitSpinner(northExitSpinner, DataConstants.NORTH);

		westExitSpinner = (Spinner) layout.findViewById(R.id.westExitSpinner);
		westExitAdapter = initCellExitSpinner(westExitSpinner, DataConstants.WEST);

		eastExitSpinner = (Spinner) layout.findViewById(R.id.eastExitSpinner);
		eastExitAdapter = initCellExitSpinner(eastExitSpinner, DataConstants.EAST);

		southExitSpinner = (Spinner) layout.findViewById(R.id.southExitSpinner);
		southExitAdapter = initCellExitSpinner(southExitSpinner, DataConstants.SOUTH);

		downExitSpinner = (Spinner) layout.findViewById(R.id.downExitSpinner);
		downExitAdapter = initCellExitSpinner(downExitSpinner, DataConstants.DOWN);

		terrainSpinner = (Spinner) layout.findViewById(R.id.selectTerrainSpinner);
		initTerrainSpinner(terrainSpinner);

		regionView = (RegionView) layout.findViewById(R.id.regionView);
		initRegionView(regionView);

		eventBus.post(new CellExitTypeEvent.Load(null));
		eventBus.post(new TerrainEvent.Load(null));

		return layout;
	}

	/**
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_world_map_fragment_action_bar, menu);
	}

	/**
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e("EditWorldRegionFrag", "In onOptionsItemSelected");
		int id = item.getItemId();
		switch (id) {
			case R.id.action_show_grid:
				boolean showingGrid = regionView.isShowGrid();
				if (showingGrid) {
					item.setIcon(R.drawable.ic_grid_on_black_24dp);
					item.setTitle(R.string.label_showGrid);
				}
				else {
					item.setIcon(R.drawable.ic_grid_off_black_24dp);
					item.setTitle(R.string.label_hideGrid);
				}
				regionView.setShowGrid(!showingGrid);
				return true;
			case R.id.action_dotted_lines:
				regionView.setUseDottedLineForGrid(!regionView.isUseDottedLineForGrid());
				return true;
			case R.id.action_fill_region:
				regionView.fillRegion();
				return true;
			case R.id.action_show_palette:
				if(showingPalette) {
					item.setIcon(R.drawable.ic_hdr_on_black_24dp);
					item.setTitle(R.string.label_show_palette);
				}
				else {
					item.setIcon(R.drawable.ic_hdr_off_black_24dp);
					item.setTitle(R.string.label_hide_palette);
				}
				showingPalette = !showingPalette;
				selectorsGrid.setVisibility(showingPalette ? View.VISIBLE : View.GONE);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (region != null) {
			outState.putInt(CURRENT_REGION_ID, region.getId());
		}
	}

	@Override
	public void onPause() {
		regionView.onPause();
		if(eventBus != null && eventBus.isRegistered(this)) {
			eventBus.unregister(this);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(eventBus != null && !eventBus.isRegistered(this)) {
			eventBus.register(this);
		}
		regionView.onResume();
	}

	/**
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		this.region = null;
	}

	/**
	 * Re-sizes the current {@code Map} and forces an update of the {@code MapView}.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.MapWidthChangedEvent event) {
//        region.resizeRegion();
//    }

	/**
	 * Re-sizes the current {@code Map} and forces an update of the {@code MapView}.
	 *
	 * @param event a {@code MapHeightChangedEvent}.
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.MapHeightChangedEvent event) {
//        region.resizeRegion();
//    }

	/**
	 * Displays a toast to inform the user the {@code MapCell} was saved or failed.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(final Events.SaveMapCellCompletedEvent event) {
//        String message;
//        int x = event.xCoordinate;
//        int y = event.yCoordinate;
//        @OriginLocation int origin = region.getParent().getMapOriginPosition();
//        if(origin == SOUTHEAST || origin == NORTHEAST) {
//            x = (region.getWidth() - 1) - x;
//        }
//        if(origin == SOUTHEAST || origin == SOUTHWEST) {
//            y = (region.getHeight() - 1) - y;
//        }
//        if(event.successful) {
//            mapView.updateCell(event.region.getCell(event.xCoordinate, event.yCoordinate));
//            message = String.format(getString(R.string.message_mapCellSaved),
//                    x,
//                    y,
//                    event.region.getName());
//        }
//        else {
//            message = String.format(getString(R.string.message_mapCellSaveFailed),
//                    x,
//                    y,
//                    event.region.getName());
//            Log.e(((Object)this).getClass().getName(), message +
//                System.getProperty("line.separator"), event.exception);
//        }
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//    }

	/**
	 * Displays a toast to inform the user the {@code Map} was saved or failed.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.SaveMapCompletedEvent event) {
//        String message;
//        if(event.successful) {
//            eventBus.post(new Events.LoadMapsEvent(world));
//            message = String.format(getString(R.string.message_mapSaved),
//                    event.region.getName(),
//                    event.world.getName());
//        }
//        else {
//            message = String.format(getString(R.string.message_mapSaveFailed),
//                    event.region.getName(),
//                    event.world.getName());
//            Log.e(((Object)this).getClass().getName(), message +
//                    System.getProperty("line.separator"), event.exception);
//        }
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//    }

	/**
	 * Displays a toast to inform the user the {@code MapCell} instances were loaded or failed.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.LoadMapCellsCompletedEvent event) {
//        String message;
//        if(event.successful) {
//            mapView.onRegionLoaded(event.region);
//            message = String.format(getString(R.string.message_cellsLoaded),
//                    event.region.getName());
//        }
//        else {
//            message = String.format(getString(R.string.message_cellsLoadFailed),
//                    event.region.getName());
//            Log.e(((Object)this).getClass().getName(), message +
//                    System.getProperty("line.separator"), event.exception);
//        }
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//    }
	// </editor-fold>

	// <editor-fold desc="Eventbus subscription handler methods">
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionLoaded(RegionEvent.SingleLoaded event) {
		if(event.isSuccessful()) {
			this.region = event.getRegion();
			regionNameView.setText(this.region.getName());
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSelected(RegionEvent.Selected event) {
		this.region = event.getRegion();
		if(this.region != null) {
			if (regionView != null) {
				regionView.setRegion(this.region);
			}
			regionNameView.setText(this.region.getName());
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onRegionSaved(RegionEvent.Saved event) {
		String toastString;
		Region region = event.getItem();

		if(event.isSuccessful()) {
			toastString = getString(R.string.toast_region_saved);
		}
		else {
			toastString = String.format(getString(R.string.toast_region_save_error), region != null ? region.getName() : "unknown");
		}
		Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onLoadCellExitTypesComplete(CellExitTypeEvent.Loaded event) {
		if(event.isSuccessful()) {
			for (CellExitType exit : event.getItems()) {
				upExitAdapter.add(exit);
				northExitAdapter.add(exit);
				westExitAdapter.add(exit);
				eastExitAdapter.add(exit);
				southExitAdapter.add(exit);
				downExitAdapter.add(exit);
			}
			upExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(UP, (CellExitType) upExitSpinner.getSelectedItem());
			northExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(NORTH, (CellExitType) northExitSpinner.getSelectedItem());
			westExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(WEST, (CellExitType) westExitSpinner.getSelectedItem());
			eastExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(EAST, (CellExitType) eastExitSpinner.getSelectedItem());
			southExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(SOUTH, (CellExitType) southExitSpinner.getSelectedItem());
			downExitAdapter.notifyDataSetChanged();
			regionView.setCurrentCellExit(DOWN, (CellExitType) downExitSpinner.getSelectedItem());
		}
		else {
			Toast.makeText(getActivity(), getString(R.string.toast_cell_exit_types_load_error), Toast.LENGTH_SHORT).show();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onTerrainsLoadedEvent(TerrainEvent.Loaded event) {
		if(event.isSuccessful()) {
			terrainAdapter.addAll(event.getItems());
			terrainAdapter.notifyDataSetChanged();
		}
		else {
			Toast.makeText(getActivity(), getString(R.string.toast_terrains_load_error), Toast.LENGTH_SHORT).show();
		}
	}

	// </editor-fold>

	// <editor-fold desc="Public action methods">
	public void loadRegion(int regionId) {
		if(eventBus != null) {
			eventBus.post(new RegionEvent.LoadById(regionId));
		}
	}

	public void setRegion(Region region) {
		this.region = region;
		if(region != null) {
			if (regionView != null) {
				regionView.setRegion(region);
			}
			if (region.getName() != null) {
				regionNameView.setText(region.getName());
			}
		}
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">

	private void initRegionNameView(View layout) {
		regionNameView = (EditText) layout.findViewById(R.id.regionNameEdit);
		regionNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String newName = ((TextView) v).getText().toString();
				if (newName.length() == 0) {
					regionNameView.setError(getString(R.string.validation_RegionNameRequired));
				}
				else if(!newName.equals(region.getName())) {
					region.setName(newName);
					eventBus.post(new RegionEvent.Save(region));
				}
			}
		});
		if(region != null) {
			regionNameView.setText(region.getName());
		}
	}

	private CellExitSpinnerAdapter initCellExitSpinner(final Spinner spinner,
									 @DataConstants.Direction final int direction) {
		spinner.setTag(true);

		CellExitSpinnerAdapter cellExitSpinnerAdapter = new CellExitSpinnerAdapter(getActivity(),
			direction, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean sticky = (Boolean) spinner.getTag();
					ImageView imageView = (ImageView) v;
					if (sticky) {
						imageView.setImageResource(android.R.drawable.ic_secure);
					}
					else {
						imageView.setImageResource(android.R.drawable.ic_partial_secure);
					}
					spinner.setTag(!sticky);
					regionView.setStickyCellExit(direction, !sticky);
				}
			});
		spinner.setAdapter(cellExitSpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				regionView.setCurrentCellExit(direction,
										   (CellExitType) parent.getItemAtPosition(position));
			}
			@Override
			public void onNothingSelected(AdapterView<
					?> parent) {
			}
		});

		return cellExitSpinnerAdapter;
	}

	private void initTerrainSpinner(final Spinner spinner) {
		spinner.setTag(true);

		terrainAdapter = new TerrainSpinnerAdapter(
				getActivity(), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean sticky = (Boolean) spinner.getTag();
				ImageView imageView = (ImageView) v;
				if (sticky) {
					imageView.setImageResource(android.R.drawable.ic_partial_secure);
				}
				else {
					imageView.setImageResource(android.R.drawable.ic_secure);
				}
				spinner.setTag(!sticky);
				regionView.setStickyTerrain(!sticky);
			}
		});
		spinner.setAdapter(terrainAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				regionView.setCurrentTerrain((Terrain) parent.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initRegionView(RegionView regionView) {
		regionView.setTerrainChangedListener(new RegionView.OnTerrainChangedListener() {
			@Override
			public void onTerrainChanged(Terrain terrain) {
				terrainSpinner.setSelection(terrainAdapter.getPosition(terrain));
			}
		});
		regionView.setOnExitChangedListener(NORTH, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExitType cellExitType) {
				northExitSpinner.setSelection(northExitAdapter.getPosition(cellExitType));
			}
		});
		regionView.setOnExitChangedListener(WEST, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExitType cellExitType) {
				westExitSpinner.setSelection(westExitAdapter.getPosition(cellExitType));
			}
		});
		regionView.setOnExitChangedListener(EAST, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExitType cellExitType) {
				eastExitSpinner.setSelection(eastExitAdapter.getPosition(cellExitType));
			}
		});
		regionView.setOnExitChangedListener(SOUTH, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExitType cellExitType) {
				southExitSpinner.setSelection(southExitAdapter.getPosition(cellExitType));
			}
		});
		regionView.setCellChangedListener(new RegionView.OnCellChangedListener() {
			@Override
			public void onCellChanged(Region region, int xCoordinate, int yCoordinate) {
            String message = String.format(getString(R.string.message_regionCellSaved),
                    xCoordinate, yCoordinate, region.getName());
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			}
		});

		regionView.setStickyCellExit(NORTH, true);
		regionView.setStickyCellExit(WEST, true);
		regionView.setStickyCellExit(EAST, true);
		regionView.setStickyCellExit(SOUTH, true);

		if(region != null) {
			regionView.setRegion(region);
		}
		else {
			Log.e("RegionView", "region is null");
		}
	}
	// </editor-fold>
}
