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
 * Database schema data for the connected_regions table
 */
public interface ConnectedRegionsSchema extends BaseColumns {
	// Connected regions table constants
	String TABLE_NAME       			= "connected_regions";
	String REGION_ID_COLUMN_NAME        = "region_id";
	String DIRECTION_COLUMN_NAME        = "direction";
	String CONNECTED_MAP_ID_COLUMN_NAME = "connected_region_id";

	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + " (" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					REGION_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					CONNECTED_MAP_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					DIRECTION_COLUMN_NAME + " INTEGER NOT NULL, " +
					"FOREIGN KEY (" + REGION_ID_COLUMN_NAME + ") REFERENCES " + TABLE_NAME + "(" + _ID + "), " +
					"FOREIGN KEY (" + CONNECTED_MAP_ID_COLUMN_NAME + ") REFERENCES " + TABLE_NAME + "(" + _ID + "), " +
					"CONSTRAINT unique_connection_direction UNIQUE (" +
						REGION_ID_COLUMN_NAME + "," + DIRECTION_COLUMN_NAME + "));";
}
