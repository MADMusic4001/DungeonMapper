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
 * Database schema data for the cell_exit_types table
 */
public interface CellExitTypeSchema extends BaseColumns {
	// Cell exits table constants
	String   TABLE_NAME               = "cell_exit_types";
	String   NAME                     = "name";
	String   USER_CREATED             = "user_created";
	String   IS_SOLID                 = "is_solid";
	String[] cellExitTypesColumnNames = {
			_ID,
			NAME,
			USER_CREATED,
			IS_SOLID};
	String CREATE_TABLE             =
			"CREATE TABLE " + TABLE_NAME + " (" +
					_ID + " INTEGER NOT NULL PRIMARY KEY, " +
					NAME + " TEXT NOT NULL, " +
					USER_CREATED + " BOOLEAN NOT NULL " +
					"CHECK (" + USER_CREATED + " IN (0,1)), " +
					IS_SOLID + " BOOLEAN NOT NULL " +
					"CHECK (" + IS_SOLID + " IN (0,1)));";
	int      ID_INDEX                            = 0;
	int      NAME_INDEX                          = 1;
	int      USER_CREATED_INDEX                  = 2;
	int      SOLID_INDEX                         = 3;
}
