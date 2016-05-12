/**
 * Copyright (C) 2015 MadMusic4001
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
package com.madmusic4001.dungeonmapper.data.dao;

import android.provider.BaseColumns;

import com.madmusic4001.dungeonmapper.data.entity.Cell;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/2/2015.
 */
public interface CellDao extends BaseDao<Cell> {
	public static abstract class CellsContract implements BaseColumns {
		public static final String TABLE_NAME   = "cells";
		public static final String QUALIFIED_ID = TABLE_NAME + "." + _ID;
		public static final String REGION_ID    = "region_id";
		public static final String TERRAIN_ID   = "terrain_id";
		public static final String IS_SOLID     = "is_solid";
		public static final String X_COORDINATE = "x_coordinate";
		public static final String Y_COORDINATE = "y_coordinate";
	}
	public static abstract class RegionCellExitsContract implements BaseColumns {
		public static final String TABLE_NAME   = "region_cell_exits";
		public static final String CELL_ID      = "cell_id";
		public static final String DIRECTION    = "direction";
		public static final String CELL_EXIT_ID = "cell_exit_id";
	}
}
