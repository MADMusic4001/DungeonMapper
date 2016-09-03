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
 * Database schema data for the regions table
 */
public interface RegionSchema extends BaseColumns {
	// Regions table constants
	String TABLE_NAME = 	"regions";
	String WORLD_ID_COLUMN_NAME    = "world_id";
	String NAME_COLUMN_NAME        = "name";
	String CREATE_TS_COLUMN_NAME   = "create_ts";
	String MODIFIED_TS_COLUMN_NAME = "modified_ts";

	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + "(" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					WORLD_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					NAME_COLUMN_NAME + " TEXT NOT NULL, " +
					CREATE_TS_COLUMN_NAME + " LONG NOT NULL, " +
					MODIFIED_TS_COLUMN_NAME + " LONG NOT NULL, " +
					"FOREIGN KEY (" + WORLD_ID_COLUMN_NAME +") REFERENCES " +
					WorldSchema.TABLE_NAME + "(" + WorldSchema._ID + "), " +
					"CONSTRAINT unique_world_region_name UNIQUE (" + WORLD_ID_COLUMN_NAME + "," + NAME_COLUMN_NAME + "));";

	String[] regionColumns     = {
			_ID,
			WORLD_ID_COLUMN_NAME,
			NAME_COLUMN_NAME,
			CREATE_TS_COLUMN_NAME,
			MODIFIED_TS_COLUMN_NAME
	};

	int      REGION_ID_INDEX   = 0;
	int      WORLD_ID_INDEX    = 1;
	int      NAME_INDEX        = 2;
	int      CREATE_TS_INDEX   = 3;
	int      MODIFIED_TS_INDEX = 4;
}
