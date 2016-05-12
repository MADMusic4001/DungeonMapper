package com.madmusic4001.dungeonmapper.view.activities.editTerrain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.madmusic4001.dungeonmapper.R;

/**
 * An activity representing a list of Terrains. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TerrainDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link EditTerrainListFragment} and the item details
 * (if present) is a {@link TerrainDetailFragment}.
 * <p/>
 */
public class EditTerrainActivity extends Activity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_terrains);

		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (findViewById(R.id.terrainDetailContainer) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp-port). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((EditTerrainListFragment) getFragmentManager()
					.findFragmentById(R.id.editTerrainsFragmentContainer))
					.setActivateOnItemClick(true);
		}
	}

	/**
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_terrains_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this, EditTerrainActivity.class));
			result = true;
		}
		return result || super.onOptionsItemSelected(item);
	}

	/**
	 * Handles request to show Terrain details.
	 *
	 * @param event a {@code Events.TerrainSelectedEvent}.
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(Events.TerrainSelectedEvent event) {
//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putLong(TerrainDetailFragment.ARG_ITEM_ID, event.id);
//            TerrainDetailFragment fragment = new TerrainDetailFragment();
//            fragment.setArguments(arguments);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.terrainDetailContainer, fragment)
//                    .commit();
//        } else {
//            // In single-pane mode, simply start the detail activity
//            // for the selected item ID.
//            Intent detailIntent = new Intent(this, TerrainDetailActivity.class);
//            detailIntent.putExtra(TerrainDetailFragment.ARG_ITEM_ID, event.id);
//            startActivity(detailIntent);
//        }
//    }
}
