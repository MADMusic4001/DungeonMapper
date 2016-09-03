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

package com.madmusic4001.dungeonmapper.data.dao.impl.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.schemas.WorldSchema;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;

/**
 * Implementation of the {@link WorldDao} for managing {@link World} instances in a SQLite database.
 */
@Singleton
public class WorldDaoSqlImpl extends BaseDaoSql implements WorldDao, WorldSchema {
	// Member variables
	private SQLiteOpenHelper sqlHelper;

	/**
	 * Creates a new WorldDaoSqlImpl instance with the give SQLiteOpenHelper.
	 *
	 * @param sqlHelper  a SQLiteOpenHelp instance
	 */
	public WorldDaoSqlImpl(SQLiteOpenHelper sqlHelper) {
		this.sqlHelper = sqlHelper;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		return 0;
	}

	@Override
	public World load(int id) {
		World theWorld = null;

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(TABLE_NAME,
									 columnNames,
									 _ID + "=?",
									 new String[]{String.valueOf(id)},
									 null,
									 null,
									 null);
			if (cursor.moveToFirst()) {
				theWorld = createInstance(cursor);
			}
			cursor.close();
			if(newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}

		return theWorld;
	}

	@Override
	public Collection<World> load(Collection<DaoFilter> filters) {
		Collection<World> worlds = null;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(TABLE_NAME,
									 columnNames,
									 whereClause,
									 whereArgsList.toArray(whereArgs),
									 null,
									 null,
									 null);
			if(cursor != null && !cursor.isClosed()) {
				worlds = new HashSet<>();
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					World aWorld = createInstance(cursor);
					worlds.add(aWorld);
					cursor.moveToNext();
				}
				cursor.close();
				if(newTransaction) {
					db.setTransactionSuccessful();
				}
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}
		return worlds;
	}

	@Override
	public boolean save(World aWorld) {
		boolean result = false;
		ContentValues values = new ContentValues();

		values.put(NAME_COLUMN_NAME, aWorld.getName());
		values.put(REGION_ORIGIN_OFFSET_COLUMN_NAME, aWorld.getOriginOffset());
		values.put(REGION_ORIGIN_POSITION_COLUMN_NAME, aWorld.getOriginLocation());
		values.put(REGION_WIDTH_COLUMN_NAME, aWorld.getRegionWidth());
		values.put(REGION_HEIGHT_COLUMN_NAME, aWorld.getRegionHeight());
		values.put(CREATE_TS_COLUMN_NAME, aWorld.getCreateTs().getTimeInMillis());
		values.put(MODIFIED_TS_COLUMN_NAME, Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			if (aWorld.getId() == -1) {
				aWorld.setId((int) db.insert(TABLE_NAME, null, values));
				result = (aWorld.getId() != -1);
			}
			else {
				values.put(_ID, aWorld.getId());
				int count = db.update(TABLE_NAME, values, _ID + EQUALS + PLACEHOLDER,
								  new String[]{Long.toString(aWorld.getId())});
				result = (count == 1);
			}
			if(result && newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}
		return result;
	}

	@Override
	public int delete(Collection<DaoFilter> filters) {
		int result = -1;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			result = db.delete(TABLE_NAME,
							whereClause,
							whereArgsList.toArray(whereArgs));
			if(result  >= 0 && newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		finally {
			if(newTransaction) {
				db.endTransaction();
			}
		}
		return result;
	}

	private World createInstance(Cursor cursor) {
		Calendar cal;
		World aWorld = new World(cursor.getString(NAME_INDEX));

		aWorld.setId(cursor.getInt(ID_INDEX));
		aWorld.setOriginOffset(cursor.getInt(ORIGIN_OFFSET_INDEX));
		@OriginLocation int tempOrigin = cursor.getInt(ORIGIN_POSITION_INDEX);
		aWorld.setOriginLocation(tempOrigin);
		aWorld.setRegionWidth(cursor.getInt(MAP_WIDTH_INDEX));
		aWorld.setRegionHeight(cursor.getInt(MAP_HEIGHT_INDEX));
		cal = Calendar.getInstance();
		cal.setTimeInMillis(cursor.getLong(CREATE_TS_INDEX));
		aWorld.setCreateTs(cal);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(cursor.getLong(MODIFIED_TS_INDEX));
		aWorld.setModifiedTs(cal);

		return aWorld;
	}
}
