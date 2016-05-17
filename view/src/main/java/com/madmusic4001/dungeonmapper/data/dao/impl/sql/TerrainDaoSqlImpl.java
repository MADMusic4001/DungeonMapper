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
import android.provider.BaseColumns;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
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
public class TerrainDaoSqlImpl extends BaseDaoSql implements TerrainDao {
	// Terrain table constants
	public static abstract class TerrainsContract implements BaseColumns {
		public static final String TABLE_NAME = "terrains";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_USER_CREATED = "user_created";
		public static final String COLUMN_NAME_SOLID = "is_solid";
		public static final String COLUMN_NAME_CONNECT = "can_connect";
		public static final String COLUMN_NAME_BITMAP_DATA = "bitmap_data";
	}
	public static final String CREATE_TABLE_TERRAINS              =
		CREATE_TABLE + TerrainsContract.TABLE_NAME + "(" +
			TerrainsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			TerrainsContract.COLUMN_NAME_NAME + TEXT + NOT_NULL + COMMA +
			TerrainsContract.COLUMN_NAME_USER_CREATED + BOOLEAN + NOT_NULL +
				CHECK + "(" + TerrainsContract.COLUMN_NAME_USER_CREATED + IN + "(0,1))" + COMMA +
			TerrainsContract.COLUMN_NAME_SOLID + BOOLEAN + NOT_NULL +
				CHECK + "(" + TerrainsContract.COLUMN_NAME_SOLID + IN + "(0,1))" + COMMA +
			TerrainsContract.COLUMN_NAME_CONNECT + BOOLEAN + NOT_NULL +
				CHECK + "(" + TerrainsContract.COLUMN_NAME_CONNECT + IN + "(0,1))" + COMMA +
			TerrainsContract.COLUMN_NAME_BITMAP_DATA + BLOB + ");";
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

	// Terrain display name table constants
	public static abstract class TerrainDisplayNamesContract implements BaseColumns {
		public static final String TABLE_NAME = "terrain_display_names";
		public static final String COLUMN_NAME_TERRAIN_ID = "terrain_id";
		public static final String COLUMN_NAME_LANGUAGE_CODE = "language_code";
		public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
	}
	public static final String CREATE_TABLE_TERRAIN_DISPLAY_NAMES =
		CREATE_TABLE + TerrainDisplayNamesContract.TABLE_NAME + "(" +
			TerrainDisplayNamesContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID + INTEGER + NOT_NULL + COMMA +
			TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE  + TEXT + NOT_NULL + COMMA +
			TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME + TEXT + NOT_NULL + COMMA +
			CONSTRAINT + "fk_display_name_to_terrain" +
				FOREIGN_KEY + "(" + CellDaoSqlImpl.CellsContract.TERRAIN_ID_COLUMN_NAME + ")" +
				REFERENCES + TerrainsContract.TABLE_NAME + "(" + TerrainsContract._ID +")" + ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_terrain_language_code" +
				UNIQUE + "(" + TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID + COMMA +
					TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE + "));";
	private static       String[]                  terrainDisplayNamesColumnNames = {
			TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE,
			TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME};
	private static final int                       LANGUAGE_CODE_INDEX            = 0;
	private static final int                       DISPLAY_NAME_INDEX             = 1;

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
			try {
				Cursor cursor = db.query(TerrainsContract.TABLE_NAME,
										 terrainColumnNames,
										 TerrainsContract._ID + "=?",
										 new String[]{String.valueOf(id)},
										 null,
										 null,
										 null);
				if (cursor.moveToFirst()) {
					terrain = createInstance(cursor);
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
			finally {
				db.endTransaction();
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
		try {
			Cursor cursor = db.query(TerrainsContract.TABLE_NAME,
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
				Terrain terrain = terrainCache.get(i);
				if (terrain.isUserCreated()) {
					loadDisplayNames(db, terrain);
				}
				else {
					setAppBitmapForDeviceResolution(terrain);
				}
			}
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}

		for(int i = 0; i < terrainCache.size(); i++) {
			terrainList.add(terrainCache.get(i));
		}
		return terrainList;
	}

	@Override
	public boolean save(Terrain terrain) {
		boolean result = false;
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

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			if (terrain.getId() == -1) {
				terrain.setId((int) db.insert(TerrainsContract.TABLE_NAME, null, values));
				result = (terrain.getId() != DataConstants.UNINITIALIZED);
			}
			else {
				result = (db.update(TerrainsContract.TABLE_NAME, values,
							  TerrainsContract._ID + " = ?",
							  new String[]{Long.toString(terrain.getId())}) == 1);
			}

			if(result) {
				values.clear();
				values.put(TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID, terrain.getId());
				for (Map.Entry<String, String> entry : terrain.getLocaleDisplayNames().entrySet()) {
					values.put(TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE,
							   entry.getKey());
					values.put(TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME,
							   entry.getValue());
					result &= (db.insertWithOnConflict(TerrainDisplayNamesContract.TABLE_NAME, null,
												values, SQLiteDatabase.CONFLICT_REPLACE) != -1);
				}
			}
			if(result) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			db.endTransaction();
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
		try {
			result = db.delete(TerrainsContract.TABLE_NAME,
								whereClause,
								whereArgsList.toArray(whereArgs));
			if(result >= 0) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			db.endTransaction();
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
