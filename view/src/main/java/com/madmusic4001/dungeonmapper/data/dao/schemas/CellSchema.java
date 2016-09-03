/**
 * Copyright (C) 2016 MadInnovations
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
package com.madmusic4001.dungeonmapper.data.dao.schemas;

import android.provider.BaseColumns;

/**
 * Database schema data for the cells table
 */
public interface CellSchema extends BaseColumns {
	// Cell table constants
	String TABLE_NAME   = "cells";
	String QUALIFIED_ID = TABLE_NAME + "." + _ID;
	String REGION_ID_COLUMN_NAME    = "region_id";
	String TERRAIN_ID_COLUMN_NAME   = "terrain_id";
	String IS_SOLID_COLUMN_NAME     = "is_solid";
	String X_COORDINATE_COLUMN_NAME = "x_coordinate";
	String Y_COORDINATE_COLUMN_NAME = "y_coordinate";

	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + "(" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					REGION_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					TERRAIN_ID_COLUMN_NAME + " INTEGER, " +
					IS_SOLID_COLUMN_NAME + " BOOLEAN NOT NULL " +
						"CHECK(" + IS_SOLID_COLUMN_NAME + " IN (0,1)), " +
					X_COORDINATE_COLUMN_NAME + " INTEGER NOT NULL, " +
					Y_COORDINATE_COLUMN_NAME + " INTEGER NOT NULL, " +
					"FOREIGN KEY (" + REGION_ID_COLUMN_NAME + ") REFERENCES " +
						RegionSchema.TABLE_NAME + "(" + RegionSchema._ID + "), " +
					"FOREIGN KEY (" + TERRAIN_ID_COLUMN_NAME + ") REFERENCES " +
						TerrainSchema.TABLE_NAME + "(" + TerrainSchema._ID + "), " +
					"CONSTRAINT unique_map_coordinates UNIQUE (" +
						REGION_ID_COLUMN_NAME + "," + X_COORDINATE_COLUMN_NAME + "," + Y_COORDINATE_COLUMN_NAME + "));";
}
