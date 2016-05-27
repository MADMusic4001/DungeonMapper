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
import android.util.Log;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of the {@link RegionDao} for managing {@link Region} instances in a SQLite database.
 */
@Singleton
public class RegionDaoSqlImpl extends BaseDaoSql implements RegionDao {
	// Regions table constants
	public static abstract class RegionsContract implements BaseColumns {
		public static final String TABLE_NAME = 	"regions";
		public static final String WORLD_ID_COLUMN_NAME    = "world_id";
		public static final String NAME_COLUMN_NAME        = "name";
		public static final String CREATE_TS_COLUMN_NAME   = "create_ts";
		public static final String MODIFIED_TS_COLUMN_NAME = "modified_ts";
	}
	public static final String CREATE_TABLE_REGIONS =
		CREATE_TABLE + RegionsContract.TABLE_NAME + "(" +
			RegionsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			RegionsContract.WORLD_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			RegionsContract.NAME_COLUMN_NAME + TEXT + NOT_NULL + COMMA +
			RegionsContract.CREATE_TS_COLUMN_NAME + LONG + NOT_NULL + COMMA +
			RegionsContract.MODIFIED_TS_COLUMN_NAME + LONG + NOT_NULL + COMMA +
			CONSTRAINT + "fk_map_to_world" +
				FOREIGN_KEY + "(" + RegionsContract.WORLD_ID_COLUMN_NAME +")" +
				REFERENCES + WorldDaoSqlImpl.WorldsContract.TABLE_NAME + "(" + WorldDaoSqlImpl.WorldsContract._ID + ")" +
					ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_world_map_name" +
				UNIQUE + "(" + RegionsContract.WORLD_ID_COLUMN_NAME + COMMA + RegionsContract._ID + "));";

	// Connected regions table constants
	public static abstract class ConnectedRegionsContract implements BaseColumns {
		public static final String TABLE_NAME       			= "connected_regions";
		public static final String REGION_ID_COLUMN_NAME        = "region_id";
		public static final String DIRECTION_COLUMN_NAME        = "direction";
		public static final String CONNECTED_MAP_ID_COLUMN_NAME = "connected_region_id";
	}
	public static final String CREATE_TABLE_CONNECTED_REGIONS        =
		CREATE_TABLE + ConnectedRegionsContract.TABLE_NAME + " (" +
			ConnectedRegionsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			ConnectedRegionsContract.REGION_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			ConnectedRegionsContract.CONNECTED_MAP_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			ConnectedRegionsContract.DIRECTION_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CONSTRAINT + "fk_map_to_map" +
				FOREIGN_KEY + "(" + ConnectedRegionsContract.REGION_ID_COLUMN_NAME + ")" +
				REFERENCES + RegionsContract.TABLE_NAME + "(" + RegionsContract._ID + ")" + ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "fk_map_to_connected_map" +
				FOREIGN_KEY + "(" + ConnectedRegionsContract.CONNECTED_MAP_ID_COLUMN_NAME + ")" +
				REFERENCES + RegionsContract.TABLE_NAME + "(" + RegionsContract._ID + ")" + ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_connection_direction" +
				UNIQUE + "(" + ConnectedRegionsContract.REGION_ID_COLUMN_NAME + COMMA +
					ConnectedRegionsContract.DIRECTION_COLUMN_NAME + "));";
	private              String[] regionColumns     = {
			RegionsContract._ID,
			RegionsContract.WORLD_ID_COLUMN_NAME,
			RegionsContract.NAME_COLUMN_NAME,
			RegionsContract.CREATE_TS_COLUMN_NAME,
			RegionsContract.MODIFIED_TS_COLUMN_NAME
	};
	private static final int      REGION_ID_INDEX   = 0;
	private static final int      WORLD_ID_INDEX    = 1;
	private static final int      NAME_INDEX        = 2;
	private static final int      CREATE_TS_INDEX   = 3;
	private static final int      MODIFIED_TS_INDEX = 4;

	// Member variables
	private SparseArray<Region> regionCache = new SparseArray<>(10);
	private SQLiteOpenHelper sqlHelper;
	private WorldDao         worldDao;

	/**
	 * Creates a new RegionDaoSqlImpl instance.
	 *
	 * @param sqlHelper  an {@link SQLiteOpenHelper} instance
	 * @param worldDao  a {@link WorldDao} instance
	 */
	@Inject
	public RegionDaoSqlImpl(SQLiteOpenHelper sqlHelper, WorldDao worldDao) {
		this.sqlHelper = sqlHelper;
		this.worldDao = worldDao;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		throw new UnsupportedOperationException("count() is not implemented in RegionDaoSqlImpl");
	}

	@Override
	public Region load(int id) {
		throw new UnsupportedOperationException("load(String id) is not implemented in "
														+ "RegionDaoSqlImpl.");
	}

	@Override
	public Collection<Region> load(Collection<DaoFilter> filters) {
		Cursor cursor;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		if((whereClause != null && !whereClause.isEmpty()) || regionCache.size() == 0) {
			regionCache.clear();
			SQLiteDatabase db = sqlHelper.getReadableDatabase();
			boolean newTransaction = !db.inTransaction();
			if(newTransaction) {
				db.beginTransaction();
			}
			try {
				cursor = db.query(RegionsContract.TABLE_NAME,
								  regionColumns,
								  whereClause,
								  whereArgsList.toArray(whereArgs),
								  null,
								  null,
								  null);

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Region region = createRegionInstance(cursor);
					regionCache.append(region.getId(), region);
					cursor.moveToNext();
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
		}
		Collection<Region> regionNameCollection = new ArrayList<>(regionCache.size());
		for(int i = 0; i < regionCache.size(); i++) {
			regionNameCollection.add(regionCache.valueAt(i));
		}
		return regionNameCollection;
	}

	@Override
	public boolean save(Region aRegion) {
		boolean result = false;
		ContentValues values = new ContentValues();

		values.put(RegionsContract.WORLD_ID_COLUMN_NAME, aRegion.getParent().getId());
		values.put(RegionsContract.NAME_COLUMN_NAME, aRegion.getName());
		values.put(RegionsContract.CREATE_TS_COLUMN_NAME, aRegion.getCreateTs().getTimeInMillis());
		values.put(RegionsContract.MODIFIED_TS_COLUMN_NAME, Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransactionNonExclusive();
		}
		try {
			Log.e("RegionDaoSqlImpl", "Saving region " + aRegion);
			if (aRegion.getId() == -1L) {
				aRegion.setId((int) db.insert(RegionsContract.TABLE_NAME, null, values));
				result = (aRegion.getId() != DataConstants.UNINITIALIZED);
				if (aRegion.getId() == DataConstants.UNINITIALIZED) {
					throw new DaoException(R.string.exception_regionNotSaved);
				}
			}
			else {
				int updateCount = db.updateWithOnConflict(RegionsContract.TABLE_NAME,
										   values,
										   RegionsContract._ID + "=?",
										   new String[] {Long.toString(aRegion.getId())},
										   SQLiteDatabase.CONFLICT_IGNORE);
				result = (updateCount != 1);
				Log.e("RegionDaoSqlImpl", "Update count = " + updateCount);
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
		int result = 0;
		ArrayList<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			result = db.delete(RegionsContract.TABLE_NAME,
								whereClause,
								whereArgsList.toArray(whereArgs));
			if(result >= 0 && newTransaction) {
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

	private Region createRegionInstance(Cursor cursor) {
		Calendar cal;

		int worldId = cursor.getInt(WORLD_ID_INDEX);
		World aWorld = worldDao.load(worldId);
        Region region = new Region(cursor.getString(NAME_INDEX), aWorld);
        region.setId(cursor.getInt(REGION_ID_INDEX));
        cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(CREATE_TS_INDEX));
        region.setCreateTs(cal);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(MODIFIED_TS_INDEX));
        region.setModifiedTs(cal);

    	return region;
	}
}
