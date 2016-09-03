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

package com.madmusic4001.dungeonmapper.data.dao.impl.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.schemas.TerrainDisplayNameSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.TerrainSchema;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.ID_WHEN_RESOURCE_NOT_FOUND;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.TERRAIN_NAME_ARRAY_RESOURCE_INDEX;

/**
 * Implementation of the {@link TerrainDao} for managing {@link Terrain} instances in a SQLite database.
 */
@Singleton
public class TerrainDaoSqlImpl extends BaseDaoSql implements TerrainDao, TerrainSchema {
	// member variables
	private SparseArray<Terrain> terrainCache = new SparseArray<>(12);
	private Context context;
	private SQLiteOpenHelper sqlHelper;

	/**
	 * Creates a TerrainDaoSqlImpl instance for the given Android context and with the given DungeonMapperSqlHelper instance.
	 *
	 * @param context  the Android context to use for this instance
	 * @param sqlHelper  an SQLiteOpenHelper instance to use with this instance
	 */
	public TerrainDaoSqlImpl(Context context, SQLiteOpenHelper sqlHelper) {
		this.context = context;
		this.sqlHelper = sqlHelper;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		throw new UnsupportedOperationException("count() is not implemented for TerrainDaoSqlImpl");
	}

	@Override
	public Terrain load(int id) {
		Terrain terrain = terrainCache.get(id);

		if(terrain == null) {
			SQLiteDatabase db = sqlHelper.getReadableDatabase();
			boolean newTransaction = !db.inTransaction();
			if(newTransaction) {
				db.beginTransaction();
			}
			try {
				Cursor cursor = db.query(TABLE_NAME,
										 terrainColumnNames,
										 _ID + "=?",
										 new String[]{String.valueOf(id)},
										 null,
										 null,
										 null);
				if (cursor.moveToFirst()) {
					terrain = createInstance(cursor);
				}
				cursor.close();
				if(newTransaction) {
					db.setTransactionSuccessful();
				}
			}
			finally {
				if(newTransaction) {
					db.endTransaction();
				}
			}
		}
		return terrain;
	}

	@Override
	public Collection<Terrain> load(Collection<DaoFilter> filters) {
		ArrayList<Terrain> terrainList = new ArrayList<>();
		ArrayList<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(TABLE_NAME,
									 terrainColumnNames,
									 whereClause,
									 whereArgsList.toArray(whereArgs),
									 null,
									 null,
									 null);
			terrainCache.clear();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Terrain terrain = createInstance(cursor);
				terrainCache.put(terrain.getId(), terrain);
				cursor.moveToNext();
			}
			cursor.close();

			for (int i=0; i < terrainCache.size(); i++) {
				Terrain terrain = terrainCache.valueAt(i);
				if (terrain.isUserCreated()) {
					loadDisplayNames(db, terrain);
				}
				else {
					setAppBitmapForDeviceResolution(terrain);
				}
			}
			if(newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}

		for(int i = 0; i < terrainCache.size(); i++) {
			terrainList.add(terrainCache.valueAt(i));
		}
		return terrainList;
	}

	@Override
	public boolean save(Terrain terrain) {
		boolean result = false;
		ContentValues values = new ContentValues();

		values.put(COLUMN_NAME_NAME, terrain.getName());
		values.put(COLUMN_NAME_USER_CREATED, terrain.isUserCreated());
		values.put(COLUMN_NAME_SOLID, terrain.isSolid());
		values.put(COLUMN_NAME_CONNECT, terrain.canConnect());
		if(terrain.isUserCreated()) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			terrain.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
			values.put(COLUMN_NAME_BITMAP_DATA, stream.toByteArray());
		}
		else {
			values.putNull(COLUMN_NAME_BITMAP_DATA);
		}

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			db.beginTransaction();
			if (terrain.getId() == -1) {
				terrain.setId((int) db.insert(TABLE_NAME, null, values));
				result = (terrain.getId() != DataConstants.UNINITIALIZED);
			}
			else {
				result = (db.update(TABLE_NAME, values,
							  _ID + " = ?",
							  new String[]{Long.toString(terrain.getId())}) == 1);
			}

			if(result) {
				values.clear();
				values.put(TerrainDisplayNameSchema.COLUMN_NAME_TERRAIN_ID, terrain.getId());
				for (Map.Entry<String, String> entry : terrain.getLocaleDisplayNames().entrySet()) {
					values.put(TerrainDisplayNameSchema.COLUMN_NAME_LANGUAGE_CODE,
							   entry.getKey());
					values.put(TerrainDisplayNameSchema.COLUMN_NAME_DISPLAY_NAME,
							   entry.getValue());
					result &= (db.insertWithOnConflict(TerrainDisplayNameSchema.TABLE_NAME, null,
												values, SQLiteDatabase.CONFLICT_REPLACE) != -1);
				}
			}
			if(result && newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}
		return result;
	}

	@Override
	public int delete(Collection<DaoFilter> filters) {
		int result = 0;
		ArrayList<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			result = db.delete(TABLE_NAME,
								whereClause,
								whereArgsList.toArray(whereArgs));
			if(result >= 0 && newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}

		return result;
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
		Cursor cursor = db.query(TerrainDisplayNameSchema.TABLE_NAME,
								 TerrainDisplayNameSchema.terrainDisplayNamesColumnNames,
								 TerrainDisplayNameSchema.COLUMN_NAME_TERRAIN_ID + "= ?",
                				 new String[]{Long.toString(aTerrain.getId())},
                				 null,
                				 null,
                				 null);

			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				aTerrain.addDisplayName(
                        cursor.getString(TerrainDisplayNameSchema.LANGUAGE_CODE_INDEX),
                        cursor.getString(TerrainDisplayNameSchema.DISPLAY_NAME_INDEX));
				cursor.moveToNext();
			}
			cursor.close();
	}

	@SuppressWarnings("ResourceType")
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
