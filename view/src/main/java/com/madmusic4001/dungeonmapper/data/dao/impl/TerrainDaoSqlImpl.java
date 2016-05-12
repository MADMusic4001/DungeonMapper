/**
 * Copyright (C) 2014 MadMusic4001
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.madmusic4001.dungeonmapper.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.ID_WHEN_RESOURCE_NOT_FOUND;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants
		.TERRAIN_NAME_ARRAY_RESOURCE_INDEX;

/**
 *
 */
@Singleton
public class TerrainDaoSqlImpl extends BaseDaoSqlImpl implements TerrainDao {
	private static       String[]                  terrainColumnNames             = {
			TerrainsContract._ID,
			TerrainsContract.COLUMN_NAME_NAME,
			TerrainsContract.COLUMN_NAME_USER_CREATED,
			TerrainsContract.COLUMN_NAME_SOLID,
			TerrainsContract.COLUMN_NAME_CONNECT,
			TerrainsContract.COLUMN_NAME_BITMAP_DATA};
	private static final int                       ID_INDEX                       = 0;
	private static final int                       NAME_INDEX                     = 1;
	private static final int                       USER_CREATED_INDEX             = 2;
	private static final int                       SOLID_INDEX                    = 3;
	private static final int                       CONNECT_INDEX                  = 4;
	private static final int                       BITMAP_DATA_INDEX              = 5;
	private static       String[]                  terrainDisplayNamesColumnNames = {
			TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE,
			TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME};
	private static final int                       LANGUAGE_CODE_INDEX            = 0;
	private static final int                       DISPLAY_NAME_INDEX             = 1;
	private LruCache<String, Terrain> terrainCache = new LruCache<>(12);

	public TerrainDaoSqlImpl(Context context, DungeonMapperSqlHelper helper) {
		super(context, helper);
	}

	@Override
	public int count() {
		throw new UnsupportedOperationException("count() is not implemented for TerrainDaoSqlImpl");
	}

	@Override
	public Terrain load(String name) {
		Terrain terrain = terrainCache.get(name);
		if(terrain == null) {

			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query(TerrainsContract.TABLE_NAME,
									 terrainColumnNames,
									 TerrainsContract.COLUMN_NAME_NAME + "= ?",
									 new String[]{name},
									 null,
									 null,
									 null);

			if (cursor.moveToFirst()) {
				terrain = createInstance(cursor);
			}
			cursor.close();
		}
		return terrain;
	}

	@Override
	public Collection<Terrain> loadWithFilter(Terrain filter) {
		throw new UnsupportedOperationException("loadWithFilter(Terrain filter) is not "
														+ "implemented for TerrainDaoSqlImpl");
	}

	@Override
	public Collection<Terrain> loadAll() {
		List<Terrain> terrainList = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TerrainsContract.TABLE_NAME,
								 terrainColumnNames,
								 null,
								 null,
								 null,
								 null,
								 null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Terrain terrain = createInstance(cursor);
			terrainList.add(terrain);
			cursor.moveToNext();
		}
		cursor.close();

		for (Terrain terrain : terrainList) {
			if (terrain.isUserCreated()) {
				loadDisplayNames(db, terrain);
			}
			else {
				setAppBitmapForDeviceResolution(terrain);
			}
		}
		return terrainList;
	}

	@Override
	public void save(Terrain terrain) {
		ContentValues values = new ContentValues();

		values.put(TerrainsContract.COLUMN_NAME_NAME, terrain.getName());
		values.put(TerrainsContract.COLUMN_NAME_USER_CREATED, terrain.isUserCreated());
		values.put(TerrainsContract.COLUMN_NAME_SOLID, terrain.isSolid());
		values.put(TerrainsContract.COLUMN_NAME_CONNECT, terrain.canConnect());
		if(terrain.isUserCreated()) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			terrain.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
			values.put(TerrainsContract.COLUMN_NAME_BITMAP_DATA, stream.toByteArray());
		}
		else {
			values.putNull(TerrainsContract.COLUMN_NAME_BITMAP_DATA);
		}

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		if(terrain.getId() == -1) {
			terrain.setId((int)db.insert(TerrainsContract.TABLE_NAME, null, values));
			if(terrain.getId() == -1) {
				throw new DaoException(R.string.exception_terrainNotSaved);
			}
		}
		else {
			if(db.update(TerrainsContract.TABLE_NAME, values,
						 TerrainsContract._ID + " = ?",
						 new String[] {Long.toString(terrain.getId())}) != 1) {
				throw new DaoException(R.string.exception_terrainNotSaved);
			}
		}

		values.clear();
		values.put(TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID, terrain.getId());
		for(Map.Entry<String, String> entry : terrain.getLocaleDisplayNames().entrySet()) {
			values.put(TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE,
					   entry.getKey());
			values.put(TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME,
					   entry.getValue());
			if(db.insertWithOnConflict(TerrainDisplayNamesContract.TABLE_NAME, null,
									   values, SQLiteDatabase.CONFLICT_REPLACE) == -1) {
				throw new DaoException(R.string.exception_terrainNotSaved);
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void delete(Terrain aTerrain) {
		SQLiteDatabase db = getWritableDatabase();

		if(db.delete(TerrainsContract.TABLE_NAME,
					 TerrainsContract._ID + " = ?",
					 new String[] {Long.toString(aTerrain.getId())}) == 0) {
			throw new DaoException(R.string.exception_terrainNotRemoved);
		}
	}

	private Terrain createInstance(Cursor cursor) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();

		Terrain terrain = new Terrain(cursor.getString(NAME_INDEX));
		terrain.setId((int)cursor.getLong(ID_INDEX));
		terrain.setUserCreated(cursor.getInt(USER_CREATED_INDEX) != 0);
        terrain.setSolid(cursor.getInt(SOLID_INDEX) != 0);
        terrain.setConnect(cursor.getInt(CONNECT_INDEX) != 0);
        if(terrain.isUserCreated()) {
            byte[] bitmapData = cursor.getBlob(BITMAP_DATA_INDEX);
            options.inScaled = false;
            bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
            terrain.setImage(bitmap);
        }
		return terrain;
	}

	private void loadDisplayNames(SQLiteDatabase db, Terrain aTerrain) {
		Cursor cursor = db.query(TerrainDisplayNamesContract.TABLE_NAME,
                terrainDisplayNamesColumnNames,
                TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID + "= ?",
                new String[]{Long.toString(aTerrain.getId())},
                null,
                null,
                null);

			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				aTerrain.addDisplayName(
                        cursor.getString(LANGUAGE_CODE_INDEX),
                        cursor.getString(DISPLAY_NAME_INDEX));
				cursor.moveToNext();
			}
			cursor.close();
	}

	private void setAppBitmapForDeviceResolution(Terrain aTerrain) {
		Resources resources = context.getResources();
		Locale locale = Locale.getDefault();
        // When the DB is created it saves the full resource name for app defined resources but
        // for the terrain manager activity we only want to display the entry name so after we
        // get the resource ID using the full resource name we set the Terrain name member to
        // only the resource name
		int resId = resources.getIdentifier(aTerrain.getName(), null, null);
		TypedArray terrainInfo = resources.obtainTypedArray(resId);

		try {
			aTerrain.addDisplayName(locale.toString(), terrainInfo.getString(0));
            int drawableId = terrainInfo.getResourceId(TERRAIN_NAME_ARRAY_RESOURCE_INDEX,
				ID_WHEN_RESOURCE_NOT_FOUND);
            aTerrain.setAppResourceId(drawableId);
            aTerrain.setName(resources.getResourceEntryName(drawableId));
			BitmapDrawable drawable = (BitmapDrawable)terrainInfo.getDrawable(1);
			Bitmap bitmap = null;
			if(drawable != null) {
				bitmap = drawable.getBitmap();
			}
			aTerrain.setImage(bitmap);
		}
		finally {
			terrainInfo.recycle();
		}
	}
}
