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
import android.support.annotation.NonNull;

import com.madmusic4001.dungeonmapper.data.entity.CellExit;

import java.util.Map;

/**
 *
 */
public interface CellExitDao extends BaseDao<CellExit>{
	public static abstract class CellExitsContract implements BaseColumns {
		public static final String TABLE_NAME   = "cell_exits";
		public static final String NAME         = "name";
		public static final String IS_SOLID     = "is_solid";
		public static final String USER_CREATED = "user_created";
	}
	public static abstract class CellExitBitmapsContract implements BaseColumns {
		public static final String TABLE_NAME          = "cell_exit_bitmaps";
		public static final String CELL_EXIT_ID        = "cell_exit_id";
		public static final String CELL_EXIT_DIRECTION = "cell_exit_direction";
		public static final String BITMAP_DATA         = "bitmap_data";
	}
}
