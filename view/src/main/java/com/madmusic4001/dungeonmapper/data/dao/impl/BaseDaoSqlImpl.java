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
package com.madmusic4001.dungeonmapper.data.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 8/29/2014.
 */
public class BaseDaoSqlImpl {
    protected final Context                context;
    protected final DungeonMapperSqlHelper dbHelper;

    public BaseDaoSqlImpl(Context context, DungeonMapperSqlHelper helper) {
		this.context = context;
		this.dbHelper = helper;
    }

    protected SQLiteDatabase getReadableDatabase() throws DaoException {
        try {
            return dbHelper.getReadableDatabase();
        }
        catch (SQLiteException ex) {
            Log.e(WorldDaoSqlImpl.class.getName(), "Error opening SQLiteDatabase", ex);
            throw new DaoException(R.string.exception_dbAccessError, ex);
        }
    }

    protected SQLiteDatabase getWritableDatabase() throws DaoException {
        try {
            return dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            Log.e(WorldDaoSqlImpl.class.getName(), "Error opening SQLiteDatabase", ex);
            throw new DaoException(R.string.exception_dbAccessError, ex);
        }
    }
}
