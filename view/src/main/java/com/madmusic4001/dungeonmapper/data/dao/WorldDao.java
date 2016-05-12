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

import com.madmusic4001.dungeonmapper.data.entity.World;

/**
 * Interface declaring methods for managing persistent Map instances.
 */
public interface WorldDao extends BaseDao<World>{
	public static abstract class WorldsContract implements BaseColumns {
		public static final String TABLE_NAME             = "worlds";
		public static final String NAME                   = "name";
		public static final String REGION_ORIGIN_OFFSET   = "region_origin_offset";
		public static final String REGION_ORIGIN_POSITION = "region_origin_position";
		public static final String REGION_WIDTH           = "region_width";
		public static final String REGION_HEIGHT          = "region_height";
		public static final String CREATE_TS              = "create_ts";
		public static final String MODIFIED_TS            = "modified_ts";
	}
}
