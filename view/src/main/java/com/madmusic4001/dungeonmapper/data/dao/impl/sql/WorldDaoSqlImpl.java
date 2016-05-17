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
import android.provider.BaseColumns;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
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
public class WorldDaoSqlImpl extends BaseDaoSql implements WorldDao {
	// World table constants
	public static abstract class WorldsContract implements BaseColumns {
		public static final String TABLE_NAME             				= "worlds";
		public static final String NAME_COLUMN_NAME		  				= "name";
		public static final String REGION_ORIGIN_OFFSET_COLUMN_NAME		= "region_origin_offset";
		public static final String REGION_ORIGIN_POSITION_COLUMN_NAME	= "region_origin_position";
		public static final String REGION_WIDTH_COLUMN_NAME				= "region_width";
		public static final String REGION_HEIGHT_COLUMN_NAME			= "region_height";
		public static final String CREATE_TS_COLUMN_NAME				= "create_ts";
		public static final String MODIFIED_TS_COLUMN_NAME				= "modified_ts";
	}
	public static final String CREATE_TABLE_WORLDS                =
		CREATE_TABLE + WorldsContract.TABLE_NAME + " (" +
			WorldsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			WorldsContract.NAME_COLUMN_NAME + TEXT + NOT_NULL + COMMA +
			WorldsContract.REGION_ORIGIN_OFFSET_COLUMN_NAME + INTEGER + NOT_NULL +
				CHECK + "(" + WorldsContract.REGION_ORIGIN_OFFSET_COLUMN_NAME + IN + "(0,1))" + COMMA +
			WorldsContract.REGION_ORIGIN_POSITION_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			WorldsContract.REGION_WIDTH_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			WorldsContract.REGION_HEIGHT_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			WorldsContract.CREATE_TS_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			WorldsContract.MODIFIED_TS_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CONSTRAINT + "unique_world_name" +
				UNIQUE + "(" + WorldsContract.NAME_COLUMN_NAME + "));";
	private static String[] columnNames           = {
			WorldsContract._ID,
			WorldsContract.NAME_COLUMN_NAME,
			WorldsContract.REGION_ORIGIN_OFFSET_COLUMN_NAME,
			WorldsContract.REGION_ORIGIN_POSITION_COLUMN_NAME,
			WorldsContract.REGION_WIDTH_COLUMN_NAME,
			WorldsContract.REGION_HEIGHT_COLUMN_NAME,
			WorldsContract.CREATE_TS_COLUMN_NAME,
			WorldsContract.MODIFIED_TS_COLUMN_NAME};
	private static final int      ID_INDEX              = 0;
	private static final int      NAME_INDEX            = 1;
	private static final int      ORIGIN_OFFSET_INDEX   = 2;
	private static final int      ORIGIN_POSITION_INDEX = 3;
	private static final int      MAP_WIDTH_INDEX       = 4;
	private static final int      MAP_HEIGHT_INDEX      = 5;
	private static final int      CREATE_TS_INDEX       = 6;
	private static final int      MODIFIED_TS_INDEX     = 7;

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
		try {
			Cursor cursor = db.query(WorldsContract.TABLE_NAME,
									 columnNames,
									 WorldsContract._ID + "=?",
									 new String[]{String.valueOf(id)},
									 null,
									 null,
									 null);
			if (cursor.moveToFirst()) {
				theWorld = createInstance(cursor);
			}
			cursor.close();
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
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
		try {
			Cursor cursor = db.query(WorldsContract.TABLE_NAME,
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
				db.setTransactionSuccessful();
			}
		}
		finally {
			db.endTransaction();
		}
		return worlds;
	}

	@Override
	public boolean save(World aWorld) {
		boolean result = false;
		ContentValues values = new ContentValues();

		values.put(WorldsContract.NAME_COLUMN_NAME, aWorld.getName());
		values.put(WorldsContract.REGION_ORIGIN_OFFSET_COLUMN_NAME, aWorld.getOriginOffset());
		values.put(WorldsContract.REGION_ORIGIN_POSITION_COLUMN_NAME, aWorld.getOriginLocation());
		values.put(WorldsContract.REGION_WIDTH_COLUMN_NAME, aWorld.getRegionWidth());
		values.put(WorldsContract.REGION_HEIGHT_COLUMN_NAME, aWorld.getRegionHeight());
		values.put(WorldsContract.CREATE_TS_COLUMN_NAME, aWorld.getCreateTs().getTimeInMillis());
		values.put(WorldsContract.MODIFIED_TS_COLUMN_NAME, Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			if (aWorld.getId() == -1) {
				aWorld.setId((int) database.insert(WorldsContract.TABLE_NAME, null, values));
				result = (aWorld.getId() == -1);
			}
			else {
				values.put(WorldsContract._ID, aWorld.getId());
				int count = database.update(WorldsContract.TABLE_NAME, values, WorldsContract._ID + EQUALS + PLACEHOLDER,
								  new String[]{Long.toString(aWorld.getId())});
				result = (count == 1);
			}
			if(result) {
				database.setTransactionSuccessful();
			}
		}
		finally {
			database.endTransaction();
		}
		return result;
	}

	@Override
	public int delete(Collection<DaoFilter> filters) {
		int result = -1;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			result = database.delete(WorldsContract.TABLE_NAME,
							whereClause,
							whereArgsList.toArray(whereArgs));
			if(result  >= 0) {
				database.setTransactionSuccessful();
			}
		}
		finally {
			database.endTransaction();
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
