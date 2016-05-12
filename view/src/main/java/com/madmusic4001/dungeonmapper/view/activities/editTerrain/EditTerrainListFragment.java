package com.madmusic4001.dungeonmapper.view.activities.editTerrain;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.view.adapters.TerrainListAdapter;

/**
 * A list fragment representing a list of Terrains. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link TerrainDetailFragment}.
 * <p/>
 */
public class EditTerrainListFragment extends ListFragment {
	private TerrainListAdapter adapter;
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private              int    mActivatedPosition       = ListView.INVALID_POSITION;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EditTerrainListFragment() {
	}

	/**
	 * @see android.app.ListFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Ask dagger to inject dependencies
	}

	/**
	 * @see android.app.ListFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the adapter to display items in the list view.
		adapter = new TerrainListAdapter(getActivity(), R.layout.image_name_row);
		setListAdapter(adapter);

		// Create an event requesting all Terrain instances to be loaded.
//        eventBus.post(new Events.LoadTerrainsEvent());

		setHasOptionsMenu(true);
	}

	/**
	 * @see android.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	/**
	 * @see android.app.ListFragment#onCreateOptionsMenu(android.view.Menu, android.view
	 * .MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_terrains_list_fragment_action_bar, menu);
	}

	/**
	 * @see android.app.ListFragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * @see android.app.ListFragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * @see android.app.ListFragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		if (item.getItemId() == R.id.actionNewTerrain) {
//            eventBus.post(new Events.TerrainSelectedEvent(-1));
			result = true;
		}
		return result || super.onOptionsItemSelected(item);
	}

	/**
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
//        eventBus.post(new Events.TerrainSelectedEvent(adapter.getItem(position).getId()));
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if clicking an item should change its states.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick
									? ListView.CHOICE_MODE_SINGLE
									: ListView.CHOICE_MODE_NONE);
	}

	/**
	 * Sets the item in the list view that should be active.
	 *
	 * @param position the list view position to make active.
	 */
	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		}
		else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	/**
	 * Sets the data for the list adapter when the Terrain list has been loaded.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.LoadTerrainsCompletedEvent event) {
//        if(event.successful) {
//            adapter.addAll(event.terrains);
//            adapter.notifyDataSetChanged();
//        }
//    }
}
