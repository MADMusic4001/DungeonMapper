package com.madmusic4001.dungeonmapper.view.activities.editTerrain;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.view.activities.editTerrain.EditTerrainActivity;
import com.madmusic4001.dungeonmapper.view.activities.editTerrain.TerrainDetailActivity;
import com.madmusic4001.dungeonmapper.view.adapters.LocaleListAdapter;

import java.util.Locale;

import javax.inject.Inject;

/**
 * A fragment representing a single Terrain detail screen.
 * This fragment is either contained in a {@link EditTerrainActivity}
 * in two-pane mode (on tablets) or a {@link TerrainDetailActivity}
 * on handsets.
 */
public class TerrainDetailFragment extends Fragment {
	public static final int TERRAIN_IMAGE_SIZE = 144;
	@Inject
	protected TerrainManager    terrainManager;
	private   Terrain           terrain;
	private   EditText          nameEdit;
	private   Spinner           localeSpinner;
	private   LocaleListAdapter localeSpinnerAdapter;
	private   EditText          displayNameEdit;
	private   ImageView         terrainImage;

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TerrainDetailFragment() {
	}

	/**
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/**
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			long terrainId = getArguments().getLong(ARG_ITEM_ID);
			if (terrainId == -1) {
				terrain = new Terrain(getString(R.string.defaultTerrainName));
				terrain.setUserCreated(true);
			}
			else {
				terrain = terrainManager.getTerrainWithId(terrainId);
				if (terrain == null) {
					terrain = new Terrain(getString(R.string.defaultTerrainName));
					terrain.setUserCreated(true);
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.edit_terrains_details_fragment, container, false);

		nameEdit = (EditText) rootView.findViewById(R.id.nameView);
		nameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					nameEdit.setError(getString(R.string.validation_terrainNameRequired));
				}
			}
		});
		nameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String newName = v.getText().toString();
				if (newName.length() > 0 && !terrain.getName().equals(newName)) {
					if (!terrain.isUserCreated()) {
						v.setError(getString(R.string.validation_cannotModifyAppResources));
					}
					else {
						if (terrainManager.getTerrainWithName(newName) != null) {
							nameEdit.setError(getString(R.string.validation_uniqueTerrainName));
						}
						else {
							terrain.setName(v.getText().toString());
//                            if(terrain.getLocaleDisplayNames().size() > 0) {
//                                eventBus.post(new EditTerrainsEvents.SaveTerrainEvent(terrain));
//                            }
						}
					}
				}
				return true;
			}
		});

		localeSpinner = (Spinner) rootView.findViewById(R.id.localeSpinner);
		localeSpinnerAdapter = new LocaleListAdapter(getActivity());
		localeSpinnerAdapter.addAll(Locale.getAvailableLocales());
		localeSpinner.setAdapter(localeSpinnerAdapter);
		String languageCode;
		if (terrain.getLocaleDisplayNames().size() > 0) {
			languageCode = terrain.getLocaleDisplayNames().keySet().iterator().next();
		}
		else {
			languageCode = Locale.getDefault().toString();
		}
		localeSpinner.setSelection(getPositionForLocale(languageCode));

		displayNameEdit = (EditText) rootView.findViewById(R.id.displayNameEdit);
		displayNameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					displayNameEdit.setError(getString(R.string.validation_displayNameRequired));
				}
			}
		});
		displayNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String newName = v.getText().toString();
				String localeName = localeSpinner.getSelectedItem().toString();
				if (newName.length() > 0 && !newName.equals(
						terrain.getDisplayNameForLocaleName(localeName))) {
					if (!terrain.isUserCreated()) {
						v.setError(getString(R.string.validation_cannotModifyAppResources));
					}
					else {
						terrain.addDisplayName(localeName, newName);
						terrain.setName(v.getText().toString());
//                        if(terrain.getLocaleDisplayNames().size() > 0) {
//                            eventBus.post(new EditTerrainsEvents.SaveTerrainEvent(terrain));
//                        }
					}
				}
				return true;
			}
		});
		displayNameEdit.setText(terrain.getDisplayNameForLocaleName(localeSpinner.getSelectedItem
				().toString()));

		terrainImage = (ImageView) rootView.findViewById(R.id.normalDensityImage);
		terrainImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK,
										   MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);
			}
		});

		Bitmap bitmap;
		if (terrain.isUserCreated()) {
			bitmap = terrain.getImage();
			if (bitmap == null) {
				bitmap = getResourceBitmap(R.drawable.app_forest_terrain,
										   DisplayMetrics.DENSITY_DEFAULT);
			}
			terrainImage.setImageBitmap(bitmap);
		}
		else {
			nameEdit.setInputType(InputType.TYPE_NULL);
			displayNameEdit.setInputType(InputType.TYPE_NULL);
			int resourceId = terrain.getAppResourceId();
			bitmap = getResourceBitmap(resourceId, DisplayMetrics.DENSITY_DEFAULT);
			terrainImage.setImageBitmap(bitmap);
		}

		((TextView) rootView.findViewById(R.id.nameView)).setText(terrain.getName());

		return rootView;
	}

	/**
	 * Handles the result of the media picker activity.
	 *
	 * @param requestCode the id of the request.
	 * @param resultCode  the result code from the activity.
	 * @param data        the data returned from the activity.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == Activity.RESULT_OK) {
//            eventBus.post(new EditTerrainsEvents.LoadBitmapFromExternalStorageEvent(data));
//        }
	}

	/**
	 * Attempts to load the image selected by the user as a bitmap.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventAsync(EditTerrainsEvents.LoadBitmapFromExternalStorageEvent event) {
//        Uri selectedImage = event.intent.getData();
//        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn,
//                null, null, null);
//        cursor.moveToFirst();
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String picturePath = cursor.getString(columnIndex);
//        cursor.close();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(picturePath, options);
//        if(options.outWidth != TERRAIN_IMAGE_SIZE || options.outHeight != TERRAIN_IMAGE_SIZE ) {
//            AppException exception = new AppException(R.string.validation_wrongSize,
//                    TERRAIN_IMAGE_SIZE);
//            eventBus.post(new EditTerrainsEvents.LoadBitmapFromExternalStorageCompletedEvent(
//                    exception));
//        }
//        else {
//            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//            eventBus.post(new EditTerrainsEvents.LoadBitmapFromExternalStorageCompletedEvent(
//                    bitmap));
//        }
//    }

	/**
	 * Handles update to the UI when the user finishes selecting a bitmap and the app finishes
	 * trying to load it.
	 *
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(EditTerrainsEvents.LoadBitmapFromExternalStorageCompletedEvent
//                                          event) {
//        if(event.successful) {
//            terrain.setImage(event.bitmap);
//            if(terrain.getLocaleDisplayNames().size() > 0) {
//                eventBus.post(new EditTerrainsEvents.SaveTerrainEvent(terrain));
//            }
//        }
//    }

	/**
	 * Displays a toast message to the user when a TerrainSaveEvent finishes.
	 */
//    @SuppressWarnings("unused")
//    public void onEventMainThread(EditTerrainsEvents.SaveTerrainCompletedEvent event) {
//        int stringId;
//        if(event.successful) {
//            stringId = R.string.message_terrainSaved;
//        }
//        else {
//            Log.d(this.getClass().getName(), getString(event.exception.getResourceId()),
//                    event.exception);
//            stringId = R.string.message_terrainSaveFailed;
//        }
//        Toast.makeText(getActivity(), String.format(getString(stringId),
//                event.terrain.getName()), Toast.LENGTH_SHORT).show();
//    }
	private int getPositionForLocale(@NonNull String localeString) {
		Locale[] locales = Locale.getAvailableLocales();
		String positionLocaleString;
		String englishLocaleString = Locale.ENGLISH.toString();
		int position = 0;

		for (int i = 0; i < locales.length; i++) {
			positionLocaleString = locales[i].toString();
			if (localeString.equals(positionLocaleString)) {
				position = i;
				break;
			}
			else if (englishLocaleString.equals(positionLocaleString)) {
				position = i;
			}
		}
		return position;
	}

	private Bitmap getResourceBitmap(int resourceId, int density) {
		Bitmap bitmap;
		Drawable drawable = getResources().getDrawableForDensity(resourceId, density);
		if (drawable instanceof BitmapDrawable) {
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
										 drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		}
		return bitmap;
	}
}
