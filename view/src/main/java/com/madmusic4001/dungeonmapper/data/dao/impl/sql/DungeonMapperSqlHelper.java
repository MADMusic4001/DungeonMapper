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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.schemas.CellExitTypeBitmapSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.CellExitTypeSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.CellSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.ConnectedRegionsSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.RegionCellExitSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.RegionSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.TerrainDisplayNameSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.TerrainSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.WorldSchema;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class DungeonMapperSqlHelper extends SQLiteOpenHelper {
	private static final String LOG_TAG = "DungeonMapperSqlHelper";
	private final Context context;

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#SQLiteOpenHelper(android.content.Context,
	 * String, android.database.sqlite.SQLiteDatabase.CursorFactory, int)
	 */
	@Inject
	public DungeonMapperSqlHelper(Context context) {
		super(context, DataConstants.DB_NAME, null, DataConstants.DB_VERSION);
		this.context = context;
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@SuppressWarnings("ResourceType")
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "Creating db...");
		try {
			db.beginTransaction();
			db.execSQL(TerrainSchema.CREATE_TABLE);
			db.execSQL(TerrainDisplayNameSchema.CREATE_TABLE);
			db.execSQL(WorldSchema.CREATE_TABLE);
			db.execSQL(RegionSchema.CREATE_TABLE);
			db.execSQL(ConnectedRegionsSchema.CREATE_TABLE);
			db.execSQL(CellSchema.CREATE_TABLE);
			db.execSQL(RegionCellExitSchema.CREATE_TABLE);
			db.execSQL(CellExitTypeSchema.CREATE_TABLE);
			db.execSQL(CellExitTypeBitmapSchema.CREATE_TABLE);

			ContentValues values = new ContentValues();
			values.put(TerrainSchema.COLUMN_NAME_USER_CREATED, false);
			Resources resources = context.getResources();
			// Get resource array listing app defined terrains
			TypedArray appTerrainsInfo = resources.obtainTypedArray(R.array.AppTerrainsInfo);
			try {
				int numTerrains = appTerrainsInfo.length();
				// For each terrain in the resource array
				for (int i = 0; i < numTerrains; i++) {
					// Get resource id of the resource array for an app defined terrain
					int resourceId = appTerrainsInfo.getResourceId(i,
									DataConstants.ID_WHEN_RESOURCE_NOT_FOUND);
					// Save the name of the terrain resource array as the name of the terrain
                    // in the database
                    values.put(TerrainSchema.COLUMN_NAME_NAME,
                           resources.getResourceName(resourceId));
                    TypedArray terrainInfo = resources.obtainTypedArray(resourceId);
                    try {
                        // Get the solid flag from the resource array and save to tb
                        values.put(TerrainSchema.COLUMN_NAME_SOLID,
                                terrainInfo.getBoolean(2, false));
                        // Get the connectible flag from the resource array and save to fb
                        values.put(TerrainSchema.COLUMN_NAME_CONNECT,
                                terrainInfo.getBoolean(3, false));
                        db.insert(TerrainSchema.TABLE_NAME, null, values);
                    }
                    finally {
                        terrainInfo.recycle();
                    }
				}
			}
			finally {
				appTerrainsInfo.recycle();
			}

			values.clear();
			values.put(CellExitTypeSchema.USER_CREATED, false);
            // Get resource array listing app defined cell exits
			TypedArray appCellExitTypesInfo = resources.obtainTypedArray(R.array.appCellExitTypesInfo);
			try {
				int numCellExits = appCellExitTypesInfo.length();
                // For each cell exit in the resource array
				for(int i=0; i < numCellExits; i++) {
                    // Get resource id of the resource array for an app defined cell exit
					int resourceId = appCellExitTypesInfo.getResourceId(i,
										DataConstants.ID_WHEN_RESOURCE_NOT_FOUND);
                    // Save the name of typed array resource in the NAME field of the table
                    values.put(CellExitTypeSchema.NAME,
							   resources.getResourceName(resourceId));
                    // Get the typed array for the cell exit
                    TypedArray cellExitInfo = resources.obtainTypedArray(resourceId);
                    try {
                        // Save the solid indicator
                        values.put(CellExitTypeSchema.IS_SOLID,
								   cellExitInfo.getBoolean(5, false));
                        db.insert(CellExitTypeSchema.TABLE_NAME, null, values);
                    }
                    finally {
                        cellExitInfo.recycle();
                    }
				}
			}
			finally {
				appCellExitTypesInfo.recycle();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		catch(SQLException ex) {
			Log.e(LOG_TAG, "Error creating db.", ex);
			db.endTransaction();
		}
		Log.d(LOG_TAG, "Db creation complete. Db located at " + db.getPath());
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
