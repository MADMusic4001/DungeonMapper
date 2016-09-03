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
 * Database schema data for the terrains table
 */
public interface TerrainSchema extends BaseColumns {
	// Terrain table constants
	String TABLE_NAME = "terrains";
	String COLUMN_NAME_NAME = "name";
	String COLUMN_NAME_USER_CREATED = "user_created";
	String COLUMN_NAME_SOLID = "is_solid";
	String COLUMN_NAME_CONNECT = "can_connect";
	String COLUMN_NAME_BITMAP_DATA = "bitmap_data";

	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + "(" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					COLUMN_NAME_NAME + " TEXT NOT NULL, " +
					COLUMN_NAME_USER_CREATED + " BOOLEAN NOT NULL " +
					"CHECK 	(" + COLUMN_NAME_USER_CREATED + " IN (0,1)), " +
					COLUMN_NAME_SOLID + " BOOLEAN NOT NULL " +
						" CHECK (" + COLUMN_NAME_SOLID + " IN (0,1)), " +
					COLUMN_NAME_CONNECT + " BOOLEAN NOT NULL " +
						" CHECK (" + COLUMN_NAME_CONNECT + " IN (0,1)), " +
					COLUMN_NAME_BITMAP_DATA + " BLOB);";
	String[]                  terrainColumnNames             = {
			_ID,
			COLUMN_NAME_NAME,
			COLUMN_NAME_USER_CREATED,
			COLUMN_NAME_SOLID,
			COLUMN_NAME_CONNECT,
			COLUMN_NAME_BITMAP_DATA};
	int                       ID_INDEX                       = 0;
	int                       NAME_INDEX                     = 1;
	int                       USER_CREATED_INDEX             = 2;
	int                       SOLID_INDEX                    = 3;
	int                       CONNECT_INDEX                  = 4;
	int                       BITMAP_DATA_INDEX              = 5;
}
