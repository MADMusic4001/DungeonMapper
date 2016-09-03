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
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 9/3/2016.
 */
public interface CellExitTypeBitmapSchema extends BaseColumns {
	// Cell exit bitmaps table constants
	String TABLE_NAME          = "cell_exit_type_bitmaps";
	String CELL_EXIT_ID        = "cell_exit_id";
	String CELL_EXIT_DIRECTION = "cell_exit_direction";
	String BITMAP_DATA         = "bitmap_data";
	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + "(" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					CELL_EXIT_ID + " INTEGER NOT NULL, " +
					CELL_EXIT_DIRECTION + " INTEGER NOT NULL, " +
					BITMAP_DATA + " BLOB NOT NULL, " +
					"CONSTRAINT fk_bitmap_to_cell_exit" +
					" FOREIGN KEY(" + CELL_EXIT_ID + ")" +
					" REFERENCES " + TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE, " +
					"CONSTRAINT unique_cell_exit_side_density UNIQUE (" +CELL_EXIT_ID + "," + CELL_EXIT_DIRECTION + "));";
	String[] cellExitTypeBitmapsColumnNames      = {CELL_EXIT_DIRECTION, BITMAP_DATA};
	int      DIRECTION_INDEX                     = 0;
	int      BITMAP_DATA_INDEX                   = 1;
}
