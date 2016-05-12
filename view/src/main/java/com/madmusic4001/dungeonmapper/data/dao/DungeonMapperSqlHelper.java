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

package com.madmusic4001.dungeonmapper.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class DungeonMapperSqlHelper extends SQLiteOpenHelper {
	private final Context context;

	private static final String CREATE_TABLE_WORLDS                =
			"CREATE TABLE " + WorldDao.WorldsContract.TABLE_NAME + " (" +
					WorldDao.WorldsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					WorldDao.WorldsContract.NAME + " TEXT NOT NULL, " +
					WorldDao.WorldsContract.REGION_ORIGIN_OFFSET
					+ " INTEGER NOT NULL CHECK ("
					+
					WorldDao.WorldsContract.REGION_ORIGIN_OFFSET + " IN (0,1)), " +
					WorldDao.WorldsContract.REGION_ORIGIN_POSITION + " INTEGER NOT NULL, "
					+
					WorldDao.WorldsContract.REGION_WIDTH + " INTEGER NOT NULL, " +
					WorldDao.WorldsContract.REGION_HEIGHT + " INTEGER NOT NULL, " +
					WorldDao.WorldsContract.CREATE_TS + " INTEGER NOT NULL, " +
					WorldDao.WorldsContract.MODIFIED_TS + " INTEGER NOT NULL, " +
					"CONSTRAINT unique_world_name UNIQUE ("
					+ WorldDao.WorldsContract.NAME
					+ "));";
	private static final String CREATE_TABLE_MAPS                  =
			"CREATE TABLE " + RegionDao.RegionsContract.TABLE_NAME + "(" +
					RegionDao.RegionsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					RegionDao.RegionsContract.WORLD_ID + " LONG NOT NULL, " +
					RegionDao.RegionsContract.NAME + " TEXT NOT NULL, " +
					RegionDao.RegionsContract.CREATE_TS + " LONG NOT NULL, " +
					RegionDao.RegionsContract.MODIFIED_TS + " LONG NOT NULL, " +
					"CONSTRAINT fk_map_to_world FOREIGN KEY ("
					+ RegionDao.RegionsContract.WORLD_ID +
					") REFERENCES " + WorldDao.WorldsContract.TABLE_NAME + " (" +
					WorldDao.WorldsContract._ID + ") ON DELETE CASCADE, " +
					"CONSTRAINT unique_world_map_name UNIQUE ("
					+ RegionDao.RegionsContract.WORLD_ID +
					", " + RegionDao.RegionsContract.NAME + "));";
	private static final String CREATE_TABLE_CONNECTED_MAPS        =
			"CREATE TABLE " + RegionDao.ConnectedRegionsContract.TABLE_NAME + " (" +
					RegionDao.ConnectedRegionsContract._ID
					+ " INTEGER NOT NULL PRIMARY KEY, " +
					RegionDao.ConnectedRegionsContract.REGION_ID + " INTEGER NOT NULL, " +
					RegionDao.ConnectedRegionsContract.DIRECTION + " INTEGER NOT NULL, " +
					RegionDao.ConnectedRegionsContract.CONNECTED_MAP_ID
					+ " INTEGER NOT NULL, " +
					"CONSTRAINT fk_map_to_map FOREIGN KEY (" +
					RegionDao.ConnectedRegionsContract.REGION_ID + ") REFERENCES " +
					RegionDao.RegionsContract.TABLE_NAME + " (" +
					RegionDao.RegionsContract._ID + ") ON DELETE CASCADE, " +
					"CONSTRAINT fk_map_to_connected_map FOREIGN KEY (" +
					RegionDao.ConnectedRegionsContract.CONNECTED_MAP_ID + ") REFERENCES " +
					RegionDao.RegionsContract.TABLE_NAME + " (" +
					RegionDao.RegionsContract._ID + ") ON DELETE CASCADE, " +
					"CONSTRAINT unique_connection_direction UNIQUE (" +
					RegionDao.ConnectedRegionsContract.REGION_ID + ", " +
					RegionDao.ConnectedRegionsContract.DIRECTION + "));";
	private static final String CREATE_TABLE_CELLS                 =
			"CREATE TABLE " + CellDao.CellsContract.TABLE_NAME + " (" +
					CellDao.CellsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					CellDao.CellsContract.REGION_ID + " INTEGER NOT NULL, " +
					CellDao.CellsContract.TERRAIN_ID + " INTEGER, " +
					CellDao.CellsContract.IS_SOLID + " BOOLEAN NOT NULL CHECK (" +
					CellDao.CellsContract.IS_SOLID + " IN (0,1)), " +
					CellDao.CellsContract.X_COORDINATE + " INTEGER NOT NULL, " +
					CellDao.CellsContract.Y_COORDINATE + " INTEGER NOT NULL, " +
					"CONSTRAINT fk_cell_to_map FOREIGN KEY (" + CellDao.CellsContract.REGION_ID +
					") REFERENCES " + RegionDao.RegionsContract.TABLE_NAME + " ("
					+ RegionDao.RegionsContract._ID +
					") ON DELETE CASCADE, " +
					"CONSTRAINT fk_cell_to_terrain FOREIGN KEY (" +
					CellDao.CellsContract.TERRAIN_ID + ") REFERENCES " +
					TerrainDao.TerrainsContract.TABLE_NAME + " (" +
					TerrainDao.TerrainsContract._ID +
					") ON DELETE CASCADE, " +
					"CONSTRAINT unique_map_coordinates UNIQUE (" +
					CellDao.CellsContract.REGION_ID + ", " +
					CellDao.CellsContract.X_COORDINATE + ", " +
					CellDao.CellsContract.Y_COORDINATE + "));";
	private static final String CREATE_TABLE_MAP_CELL_EXITS        =
			"CREATE TABLE " + CellDao.RegionCellExitsContract.TABLE_NAME + " (" +
					CellDao.RegionCellExitsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					CellDao.RegionCellExitsContract.CELL_ID + " INTEGER NOT NULL, " +
					CellDao.RegionCellExitsContract.DIRECTION + " INTEGER NOT NULL, " +
					CellDao.RegionCellExitsContract.CELL_EXIT_ID + " INTEGER NOT NULL, " +
					"CONSTRAINT fk_exit_to_cell FOREIGN KEY (" +
					CellDao.RegionCellExitsContract.CELL_ID + ") REFERENCES " +
					CellDao.CellsContract.TABLE_NAME + " (" + CellDao.CellsContract._ID +
					") ON DELETE CASCADE, " +
					"CONSTRAINT fk_cell_exit_to_cell FOREIGN KEY (" +
					CellDao.RegionCellExitsContract.CELL_EXIT_ID + ") REFERENCES " +
					CellExitDao.CellExitsContract.TABLE_NAME + " (" +
					CellExitDao.CellExitsContract._ID +
					") ON DELETE CASCADE, " +
					"CONSTRAINT unique_exit_direction UNIQUE (" +
					CellDao.RegionCellExitsContract.CELL_ID + ", " +
					CellDao.RegionCellExitsContract.DIRECTION + "));";
	private static final String CREATE_TABLE_TERRAINS              =
			"CREATE TABLE " + TerrainDao.TerrainsContract.TABLE_NAME + " (" +
					TerrainDao.TerrainsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					TerrainDao.TerrainsContract.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
					TerrainDao.TerrainsContract.COLUMN_NAME_USER_CREATED
					+ " BOOLEAN NOT NULL CHECK ("
					+
					TerrainDao.TerrainsContract.COLUMN_NAME_USER_CREATED + " IN (0,1)), " +
					TerrainDao.TerrainsContract.COLUMN_NAME_SOLID + " BOOLEAN NOT NULL CHECK (" +
					TerrainDao.TerrainsContract.COLUMN_NAME_SOLID + " IN (0,1)), " +
					TerrainDao.TerrainsContract.COLUMN_NAME_CONNECT + " BOOLEAN NOT NULL CHECK (" +
					TerrainDao.TerrainsContract.COLUMN_NAME_CONNECT + " IN (0,1)), " +
					TerrainDao.TerrainsContract.COLUMN_NAME_BITMAP_DATA + " BLOB);";
	private static final String CREATE_TABLE_TERRAIN_DISPLAY_NAMES =
			"CREATE TABLE " + TerrainDao.TerrainDisplayNamesContract.TABLE_NAME + "(" +
					TerrainDao.TerrainDisplayNamesContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					TerrainDao.TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID
					+ " INTEGER NOT NULL, " +
					TerrainDao.TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE
					+ " TEXT NOT NULL, " +
					TerrainDao.TerrainDisplayNamesContract.COLUMN_NAME_DISPLAY_NAME
					+ " TEXT NOT NULL, " +
					"CONSTRAINT fk_display_name_to_terrain FOREIGN KEY (" +
					CellDao.CellsContract.TERRAIN_ID + ") REFERENCES " +
					TerrainDao.TerrainsContract.TABLE_NAME + " (" + TerrainDao.TerrainsContract._ID
					+
					") ON DELETE CASCADE, " +
					"CONSTRAINT unique_terrain_language_code UNIQUE (" +
					TerrainDao.TerrainDisplayNamesContract.COLUMN_NAME_TERRAIN_ID + ", " +
					TerrainDao.TerrainDisplayNamesContract.COLUMN_NAME_LANGUAGE_CODE + "));";
	private static final String CREATE_TABLE_CELL_EXITS            =
			"CREATE TABLE " + CellExitDao.CellExitsContract.TABLE_NAME + " (" +
					CellExitDao.CellExitsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					CellExitDao.CellExitsContract.NAME + " TEXT NOT NULL, " +
					CellExitDao.CellExitsContract.USER_CREATED
					+ " BOOLEAN NOT NULL CHECK (" +
					CellExitDao.CellExitsContract.USER_CREATED + " IN (0,1)), " +
					CellExitDao.CellExitsContract.IS_SOLID + " BOOLEAN NOT NULL CHECK ("
					+
					CellExitDao.CellExitsContract.IS_SOLID + " IN (0,1)));";
	private static final String CREATE_TABLE_CELL_EXIT_BITMAPS     =
			"CREATE TABLE " + CellExitDao.CellExitBitmapsContract.TABLE_NAME + "(" +
					CellExitDao.CellExitBitmapsContract._ID + " INTEGER NOT NULL PRIMARY KEY, " +
					CellExitDao.CellExitBitmapsContract.CELL_EXIT_ID + " INTEGER NOT "
					+ "NULL, " +
					CellExitDao.CellExitBitmapsContract.CELL_EXIT_DIRECTION
					+ " INTEGER NOT NULL, " +
					CellExitDao.CellExitBitmapsContract.BITMAP_DATA + " BLOB NOT NULL, "
					+
					"CONSTRAINT fk_bitmap_to_cell_exit FOREIGN KEY (" +
					CellExitDao.CellExitBitmapsContract.CELL_EXIT_ID + ") REFERENCES " +
					CellExitDao.CellExitsContract.TABLE_NAME + " ("
					+ CellExitDao.CellExitsContract._ID +
					") ON DELETE CASCADE, " +
					"CONSTRAINT unique_cell_exit_side_density UNIQUE (" +
					CellExitDao.CellExitBitmapsContract.CELL_EXIT_ID + ", " +
					CellExitDao.CellExitBitmapsContract.CELL_EXIT_DIRECTION + "));";

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
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(this.getClass().getName(), "Creating db...");
		try {
			db.beginTransaction();
			db.execSQL(CREATE_TABLE_TERRAINS);
			db.execSQL(CREATE_TABLE_TERRAIN_DISPLAY_NAMES);
			db.execSQL(CREATE_TABLE_WORLDS);
			db.execSQL(CREATE_TABLE_MAPS);
			db.execSQL(CREATE_TABLE_CONNECTED_MAPS);
			db.execSQL(CREATE_TABLE_CELLS);
			db.execSQL(CREATE_TABLE_MAP_CELL_EXITS);
			db.execSQL(CREATE_TABLE_CELL_EXITS);
			db.execSQL(CREATE_TABLE_CELL_EXIT_BITMAPS);

			ContentValues values = new ContentValues();
			values.put(TerrainDao.TerrainsContract.COLUMN_NAME_USER_CREATED, false);
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
                    values.put(TerrainDao.TerrainsContract.COLUMN_NAME_NAME,
                           resources.getResourceName(resourceId));
                    TypedArray terrainInfo = resources.obtainTypedArray(resourceId);
                    try {
                        // Get the solid flag from the resource array and save to tb
                        values.put(TerrainDao.TerrainsContract.COLUMN_NAME_SOLID,
                                terrainInfo.getBoolean(2, false));
                        // Get the connectible flag from the resource array and save to fb
                        values.put(TerrainDao.TerrainsContract.COLUMN_NAME_CONNECT,
                                terrainInfo.getBoolean(3, false));
                        db.insert(TerrainDao.TerrainsContract.TABLE_NAME, null, values);
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
			values.put(CellExitDao.CellExitsContract.USER_CREATED, false);
            // Get resource array listing app defined cell exits
			TypedArray appCellExitsInfo = resources.obtainTypedArray(R.array.appCellExitsInfo);
			try {
				int numCellExits = appCellExitsInfo.length();
                // For each cell exit in the resource array
				for(int i=0; i < numCellExits; i++) {
                    // Get resource id of the resource array for an app defined cell exit
					int resourceId = appCellExitsInfo.getResourceId(i,
										DataConstants.ID_WHEN_RESOURCE_NOT_FOUND);
                    // Save the name of typed array resource in the NAME field of the table
                    values.put(CellExitDao.CellExitsContract.NAME,
                            resources.getResourceName(resourceId));
                    // Get the typed array for the cell exit
                    TypedArray cellExitInfo = resources.obtainTypedArray(resourceId);
                    try {
                        // Save the solid indicator
                        values.put(CellExitDao.CellExitsContract.IS_SOLID,
                                cellExitInfo.getBoolean(5, false));
                        db.insert(CellExitDao.CellExitsContract.TABLE_NAME, null, values);
                    }
                    finally {
                        cellExitInfo.recycle();
                    }
				}
			}
			finally {
				appCellExitsInfo.recycle();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		catch(SQLException ex) {
			Log.e(this.getClass().getName(), "Error creating db.", ex);
			db.endTransaction();
		}
		Log.d(this.getClass().getName(), "Db creation complete. Db located at " + db.getPath());
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
		// TODO Auto-generated method stub

	}

}
