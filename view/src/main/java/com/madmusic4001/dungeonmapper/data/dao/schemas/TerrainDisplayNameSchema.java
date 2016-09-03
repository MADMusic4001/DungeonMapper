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

import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.CellDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

/**
 * Database schema data for the terrain_display_names table
 */
public interface TerrainDisplayNameSchema extends BaseColumns {
	String TABLE_NAME = "terrain_display_names";
	String COLUMN_NAME_TERRAIN_ID = "terrain_id";
	String COLUMN_NAME_LANGUAGE_CODE = "language_code";
	String COLUMN_NAME_DISPLAY_NAME = "display_name";

	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + "(" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					COLUMN_NAME_TERRAIN_ID + " INTEGER NOT NULL, " +
					COLUMN_NAME_LANGUAGE_CODE  + " TEXT NOT NULL, " +
					COLUMN_NAME_DISPLAY_NAME + " TEXT NOT NULL, " +
					"FOREIGN KEY (" + CellSchema.TERRAIN_ID_COLUMN_NAME + ") REFERENCES " +
						TerrainSchema.TABLE_NAME + "(" + TerrainSchema._ID +"), " +
					"CONSTRAINT unique_terrain_language_code UNIQUE (" +
						COLUMN_NAME_TERRAIN_ID + "," + COLUMN_NAME_LANGUAGE_CODE + "));";
	String[] terrainDisplayNamesColumnNames = {COLUMN_NAME_LANGUAGE_CODE, COLUMN_NAME_DISPLAY_NAME};
	int LANGUAGE_CODE_INDEX            = 0;
	int DISPLAY_NAME_INDEX             = 1;
}
