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

import com.madmusic4001.dungeonmapper.data.dao.impl.sql.BaseDaoSql;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.CellExitTypeTypeDaoSqlImpl;

/**
 * Database schema data for the region_cell_exits table
 */
public interface RegionCellExitSchema extends BaseColumns {
	String TABLE_NAME   = "region_cell_exits";
	String CELL_ID_COLUMN_NAME      = "cell_id";
	String DIRECTION_COLUMN_NAME    = "direction";
	String CELL_EXIT_ID_COLUMN_NAME = "cell_exit_id";

	String CREATE_TABLE =
			BaseDaoSql.CREATE_TABLE + TABLE_NAME + " (" +
					_ID + BaseDaoSql.INTEGER + BaseDaoSql.NOT_NULL + BaseDaoSql.PRIMARY_KEY + BaseDaoSql.COMMA +
					CELL_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					DIRECTION_COLUMN_NAME + " INTEGER NOT NULL, " +
					CELL_EXIT_ID_COLUMN_NAME + " INTEGER NOT NULL, " +
					"FOREIGN KEY (" + CELL_ID_COLUMN_NAME + ") REFERENCES " +
						CellSchema.TABLE_NAME + "(" + CellSchema._ID + "), " +
					"FOREIGN KEY (" + CELL_EXIT_ID_COLUMN_NAME + ") REFERENCES " +
						CellExitTypeSchema.TABLE_NAME + "(" + CellExitTypeSchema._ID + "), " +
					"CONSTRAINT unique_exit_direction UNIQUE (" +
						CELL_ID_COLUMN_NAME + "," + DIRECTION_COLUMN_NAME + "));";
	int      CELL_ID_INDEX      = 0;
	int      TERRAIN_ID_INDEX   = 1;
	int      IS_SOLID_INDEX     = 2;
	int      X_COORDINATE_INDEX = 3;
	int      Y_COORDINATE_INDEX = 4;
	int      DIRECTION_INDEX    = 5;
	int      EXIT_ID_INDEX      = 6;
	String[] PROJECTION         = {
			CellSchema.QUALIFIED_ID,
			CellSchema.TERRAIN_ID_COLUMN_NAME,
			CellSchema.IS_SOLID_COLUMN_NAME,
			CellSchema.X_COORDINATE_COLUMN_NAME,
			CellSchema.Y_COORDINATE_COLUMN_NAME,
			DIRECTION_COLUMN_NAME,
			CELL_EXIT_ID_COLUMN_NAME
	};
}
