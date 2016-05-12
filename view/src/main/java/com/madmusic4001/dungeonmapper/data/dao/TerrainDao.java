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

import android.provider.BaseColumns;

import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Map;

/**
 *
 */
public interface TerrainDao extends BaseDao<Terrain> {
	public static abstract class TerrainsContract implements BaseColumns {
		public static final String TABLE_NAME = "terrains";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_USER_CREATED = "user_created";
		public static final String COLUMN_NAME_SOLID = "is_solid";
		public static final String COLUMN_NAME_CONNECT = "can_connect";
		public static final String COLUMN_NAME_BITMAP_DATA = "bitmap_data";
	}
	public static abstract class TerrainDisplayNamesContract implements BaseColumns {
		public static final String TABLE_NAME = "terrain_display_names";
		public static final String COLUMN_NAME_TERRAIN_ID = "terrain_id";
		public static final String COLUMN_NAME_LANGUAGE_CODE = "language_code";
		public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
	}
}
