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
 * Database schema data for the worlds table
 */
public interface WorldSchema extends BaseColumns {
	// World table constants
	String TABLE_NAME             				= "worlds";
	String NAME_COLUMN_NAME		  				= "name";
	String REGION_ORIGIN_OFFSET_COLUMN_NAME		= "region_origin_offset";
	String REGION_ORIGIN_POSITION_COLUMN_NAME	= "region_origin_position";
	String REGION_WIDTH_COLUMN_NAME				= "region_width";
	String REGION_HEIGHT_COLUMN_NAME			= "region_height";
	String CREATE_TS_COLUMN_NAME				= "create_ts";
	String MODIFIED_TS_COLUMN_NAME				= "modified_ts";

	String CREATE_TABLE                         =
			"CREATE TABLE " +TABLE_NAME + " (" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					NAME_COLUMN_NAME + " TEXT NOT NULL, " +
					REGION_ORIGIN_OFFSET_COLUMN_NAME + " INTEGER NOT NULL CHECK (" +
						REGION_ORIGIN_OFFSET_COLUMN_NAME + " IN (0,1)), " +
					REGION_ORIGIN_POSITION_COLUMN_NAME + " INTEGER NOT NULL, " +
					REGION_WIDTH_COLUMN_NAME + " INTEGER NOT NULL, " +
					REGION_HEIGHT_COLUMN_NAME + " INTEGER NOT NULL, " +
					CREATE_TS_COLUMN_NAME + " INTEGER NOT NULL, " +
					MODIFIED_TS_COLUMN_NAME + " INTEGER NOT NULL);";
	String[] columnNames           = {
			_ID,
			NAME_COLUMN_NAME,
			REGION_ORIGIN_OFFSET_COLUMN_NAME,
			REGION_ORIGIN_POSITION_COLUMN_NAME,
			REGION_WIDTH_COLUMN_NAME,
			REGION_HEIGHT_COLUMN_NAME,
			CREATE_TS_COLUMN_NAME,
			MODIFIED_TS_COLUMN_NAME};
	int      ID_INDEX              = 0;
	int      NAME_INDEX            = 1;
	int      ORIGIN_OFFSET_INDEX   = 2;
	int      ORIGIN_POSITION_INDEX = 3;
	int      MAP_WIDTH_INDEX       = 4;
	int      MAP_HEIGHT_INDEX      = 5;
	int      CREATE_TS_INDEX       = 6;
	int      MODIFIED_TS_INDEX     = 7;
}
