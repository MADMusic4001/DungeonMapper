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
import com.madmusic4001.dungeonmapper.controller.rxhandlers.CellExitTypeRxHandler;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.RegionRxHandler;
import com.madmusic4001.dungeonmapper.controller.rxhandlers.TerrainRxHandler;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.view.adapters.CellExitSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.adapters.TerrainSpinnerAdapter;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;
import com.madmusic4001.dungeonmapper.view.views.RegionView;

import java.util.Collection;

import javax.inject.Inject;

import rx.Subscriber;

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
public class EditRegionFragment extends Fragment {
	private static final String LOG_TAG = "EditRegionFragmet";
	@Inject
	protected FilterCreator                 filterCreator;
	@Inject
	protected RegionRxHandler               regionRxHandler;
	@Inject
	protected CellExitTypeRxHandler         cellExitTypeRxHandler;
	@Inject
	protected TerrainRxHandler              terrainRxHandler;
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

	// <editor-fold desc="Public API methods">
	public void loadRegion(int regionId) {
		regionRxHandler.getRegion(regionId)
				.subscribe(new Subscriber<Region>() {
					@Override
					public void onCompleted() {}
					@Override
					public void onError(Throwable e) {
						Log.e(LOG_TAG, "Exception caught loading Region instance.", e);
					}
					@Override
					public void onNext(Region region) {
						setRegion(region);
					}
				});
	}

	public void setRegion(@NonNull Region region) {
		this.region = region;
		if (regionView != null) {
			regionView.setRegion(region);
		}
		if (region.getName() != null && regionNameView != null) {
			regionNameView.setText(region.getName());
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.edit_region_fragment, container, false);
		((EditWorldActivity) getActivity()).getActivityComponent().
				newFragmentComponent(new FragmentModule(this)).injectInto(this);

		if(savedInstanceState != null) {
			int regionId = savedInstanceState.getInt(CURRENT_REGION_ID, DataConstants.UNINITIALIZED);
			if(regionId != DataConstants.UNINITIALIZED) {
				loadRegion(regionId);
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

		cellExitTypeRxHandler.get(null)
				.subscribe(new Subscriber<Collection<CellExitType>>() {
					@Override
					public void onCompleted() {}
					@Override
					public void onError(Throwable e) {
						Log.e(LOG_TAG, "Exception caught loading CellExitType instances.", e);
						Toast.makeText(getActivity(), getString(R.string.toast_cell_exit_types_load_error), Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNext(Collection<CellExitType> cellExitTypes) {
						upExitAdapter.clear();
						upExitAdapter.addAll(cellExitTypes);
						upExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(UP, (CellExitType) upExitSpinner.getSelectedItem());

						northExitAdapter.clear();
						northExitAdapter.addAll(cellExitTypes);
						northExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(NORTH, (CellExitType) northExitSpinner.getSelectedItem());

						westExitAdapter.clear();
						westExitAdapter.addAll(cellExitTypes);
						westExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(WEST, (CellExitType) westExitSpinner.getSelectedItem());

						eastExitAdapter.clear();
						eastExitAdapter.addAll(cellExitTypes);
						eastExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(EAST, (CellExitType) eastExitSpinner.getSelectedItem());

						southExitAdapter.clear();
						southExitAdapter.addAll(cellExitTypes);
						southExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(SOUTH, (CellExitType) southExitSpinner.getSelectedItem());

						downExitAdapter.clear();
						downExitAdapter.addAll(cellExitTypes);
						downExitAdapter.notifyDataSetChanged();
						regionView.setCurrentCellExit(DOWN, (CellExitType) downExitSpinner.getSelectedItem());
					}
				});

		terrainRxHandler.load(null)
				.subscribe(new Subscriber<Collection<Terrain>>() {
					@Override
					public void onCompleted() {}
					@Override
					public void onError(Throwable e) {
						Log.e(LOG_TAG, "Exception caught loading Terrain instances.", e);
						Toast.makeText(getActivity(), getString(R.string.toast_terrains_load_error), Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNext(Collection<Terrain> terrains) {
						terrainAdapter.clear();
						terrainAdapter.addAll(terrains);
						terrainAdapter.notifyDataSetChanged();
					}
				});

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

	/**
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		this.region = null;
	}

	// </editor-fold>

	// <editor-fold desc="Eventbus subscription handler methods">
//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void onRegionLoaded(RegionEvent.SingleLoaded event) {
//		if(event.isSuccessful()) {
//			this.region = event.getRegion();
//			regionNameView.setText(this.region.getName());
//		}
//	}

//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void onRegionSelected(RegionEvent.Selected event) {
//		this.region = event.getRegion();
//		if(this.region != null) {
//			if (regionView != null) {
//				regionView.setRegion(this.region);
//			}
//			regionNameView.setText(this.region.getName());
//		}
//	}

	// </editor-fold>

	// <editor-fold desc="private action methods">
	private void saveRegion(final Region region) {
		regionRxHandler.saveRegion(region)
				.subscribe(new Subscriber<Region>() {
					@Override
					public void onCompleted() {}
					@Override
					public void onError(Throwable e) {
						Log.e(LOG_TAG, "Exception caught saving Region instance.", e);
						Toast.makeText(getActivity(), String.format(getString(R.string.toast_region_save_error),
								region != null ? region.getName() : "unknown"), Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNext(Region region) {
						Toast.makeText(getActivity(), getString(R.string.toast_region_saved), Toast.LENGTH_SHORT).show();
						((EditWorldActivity)getActivity()).addRegionToList(region);
					}
				});
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
					saveRegion(region);
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
            String message = String.format(getString(R.string.toast_regionCellSaved),
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
