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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.EditWorldRegionController;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.adapters.CellExitSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.adapters.TerrainSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;
import com.madmusic4001.dungeonmapper.view.views.RegionView;

import java.util.Collection;

import javax.inject.Inject;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.*;

/**
 *
 */
public class EditWorldRegionFragment extends Fragment
		implements EditWorldRegionController.EditWorldRegionUpdateHandler {

	@Inject
	protected EditWorldRegionController     controller;
	private   EditWorldRegionEventsListener callbackListener;
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
	private boolean isVisible = false;
	private Region  region    = null;
	private boolean showingPalette = true;

	// <editor-fold desc="EditWorldRegionController.EditWorldRegionUpdateHandler interface implementation">
	@Override
	public void onRegionLoaded(@NonNull Region region) {
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

	@Override
	public void onRegionSaved(Region region) {
		String message = String.format(getString(R.string.message_regionSaved), region.getName(),
									   region.getParent().getName());
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		callbackListener.regionNameChanged();
	}

	@Override
	public void onLoadCellExitsComplete(Collection<CellExit> cellExits) {
		for (CellExit exit : cellExits) {
			upExitAdapter.add(exit);
			northExitAdapter.add(exit);
			westExitAdapter.add(exit);
			eastExitAdapter.add(exit);
			southExitAdapter.add(exit);
			downExitAdapter.add(exit);
		}
		upExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(UP, (CellExit) upExitSpinner.getSelectedItem());
		northExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(NORTH, (CellExit) northExitSpinner.getSelectedItem());
		westExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(WEST, (CellExit) westExitSpinner.getSelectedItem());
		eastExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(EAST, (CellExit) eastExitSpinner.getSelectedItem());
		southExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(SOUTH, (CellExit) southExitSpinner.getSelectedItem());
		downExitAdapter.notifyDataSetChanged();
		regionView.setCurrentCellExit(DOWN, (CellExit) downExitSpinner.getSelectedItem());
	}

	@Override
	public void onLoadTerrainsComplete(Collection<Terrain> terrains) {
		terrainAdapter.addAll(terrains);
		terrainAdapter.notifyDataSetChanged();
	}

	// </editor-fold>

	// <editor-fold desc="Fragment lifecycle event handlers">
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			callbackListener = (EditWorldRegionEventsListener) activity;
		}
		catch (ClassCastException ex) {
			throw new ClassCastException(activity.toString()
												 + " must implement OnEditWorldPropsEventsListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.edit_region_fragment, container, false);
		((EditWorldActivity) getActivity()).getActivityComponent().
				newFragmentComponent(new FragmentModule(this)).injectInto(this);

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

		regionView = (RegionView) layout.findViewById(R.id.mapView);
		initMapView(regionView);

		controller.loadCellExits();
		controller.loadTerrains();

		setHasOptionsMenu(true);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		controller.loadRegion(callbackListener.getWorldName(), callbackListener.getRegionName());
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
			outState.putString(SELECTED_WORLD_NAME, region.getParent().getName());
			outState.putString(SAVED_REGION_NAME, region.getName());
		}
	}

	@Override
	public void onPause() {
		regionView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		regionView.onResume();
	}

	/**
	 * Set a hint to the system about whether this fragment's UI is currently visible
	 * to the user. This hint defaults to true and is persistent across fragment instance
	 * state save and restore.
	 * <p/>
	 * <p>An app may set this to false to indicate that the fragment's UI is
	 * scrolled out of visibility or is otherwise not directly visible to the user.
	 * This may be used by the system to prioritize operations such as fragment lifecycle updates
	 * or loader ordering behavior.</p>
	 *
	 * @param isVisibleToUser true if this fragment's UI is currently visible to the user
	 *                        (default),
	 *                        false if it is not.
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (!isVisible) {
//                mapView.onResume();
				isVisible = true;
			}
		}
		else {
			if (isVisible) {
				isVisible = false;
			}
		}
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
	 * Updates the {@code MapView} with the new {@code Map} instance.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.MapChangedEvent event) {
//        this.region = event.region;
//        eventBus.post(new Events.LoadMapCellsEvent(this.region));
//        if(regionNameView != null) {
//            regionNameView.setText(this.region.getName());
//        }
//    }

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

	// <editor-fold desc="Public action methods">
	public void loadRegion(@NonNull String worldName, @NonNull String regionName) {
		controller.loadRegion(worldName, regionName);
	}
	// </editor-fold>

	// <editor-fold desc="Callback interface declaration">

	public interface EditWorldRegionEventsListener {

		/**
		 * Gets the name of the {@code World} containing the {@code Region} to display in the UI.
		 *
		 * @return the name of the {@link com.madmusic4001.dungeonmapper.data.entity.World}
		 * containing the {@link Region} to display in the UI.
		 */
		String getWorldName();

		/**
		 * Gets the currently selected {@code Region} name to display in the UI.
		 *
		 * @return the name of the currently selected {@link Region}.
		 */
		String getRegionName();

		/**
		 * Notifies that the name of a region changed
		 */
		void regionNameChanged();
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
				else if (region != null && !region.getName().equals(newName)) {
					String oldName = region.getName();
					if (region.getParent().getRegionNameMap().get(newName) != null) {
						regionNameView.setError(getString(R.string.message_uniqueRegionName));
					}
					else {
						region.setName(newName);
						controller.saveRegion(region, oldName);
					}
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
										   (CellExit) parent.getItemAtPosition(position));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
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

	private void initMapView(RegionView regionView) {
		regionView.setTerrainChangedListener(new RegionView.OnTerrainChangedListener() {
			@Override
			public void onTerrainChanged(Terrain terrain) {
				terrainSpinner.setSelection(terrainAdapter.getPosition(terrain));
			}
		});
		regionView.setOnExitChangedListener(NORTH, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExit cellExit) {
				northExitSpinner.setSelection(northExitAdapter.getPosition(cellExit));
			}
		});
		regionView.setOnExitChangedListener(WEST, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExit cellExit) {
				westExitSpinner.setSelection(westExitAdapter.getPosition(cellExit));
			}
		});
		regionView.setOnExitChangedListener(EAST, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExit cellExit) {
				eastExitSpinner.setSelection(eastExitAdapter.getPosition(cellExit));
			}
		});
		regionView.setOnExitChangedListener(SOUTH, new RegionView.OnExitChangedListener() {
			@Override
			public void onExitChanged(CellExit cellExit) {
				southExitSpinner.setSelection(southExitAdapter.getPosition(cellExit));
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
	}
	// </editor-fold>
}
