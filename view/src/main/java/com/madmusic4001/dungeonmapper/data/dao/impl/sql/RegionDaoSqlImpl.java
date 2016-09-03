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
import android.util.Log;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.schemas.RegionSchema;
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
public class RegionDaoSqlImpl extends BaseDaoSql implements RegionDao, RegionSchema {
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
		Region theRegion = null;

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(TABLE_NAME,
									 regionColumns,
									 _ID + "=?",
									 new String[]{String.valueOf(id)},
									 null,
									 null,
									 null);
			if (cursor.moveToFirst()) {
				theRegion = createRegionInstance(cursor);
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

		return theRegion;
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
				cursor = db.query(TABLE_NAME,
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

		values.put(WORLD_ID_COLUMN_NAME, aRegion.getParent().getId());
		values.put(NAME_COLUMN_NAME, aRegion.getName());
		values.put(CREATE_TS_COLUMN_NAME, aRegion.getCreateTs().getTimeInMillis());
		values.put(MODIFIED_TS_COLUMN_NAME, Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransactionNonExclusive();
		}
		try {
			if (aRegion.getId() == -1L) {
				aRegion.setId((int) db.insert(TABLE_NAME, null, values));
				result = (aRegion.getId() != DataConstants.UNINITIALIZED);
				Log.e("RegionDaoSqlImpl", "db.insert() returned " + aRegion.getId());
				if (aRegion.getId() == DataConstants.UNINITIALIZED) {
					throw new DaoException(R.string.exception_regionNotSaved);
				}
			}
			else {
				int updateCount = db.updateWithOnConflict(TABLE_NAME,
										   values,
										   _ID + "=?",
										   new String[] {Long.toString(aRegion.getId())},
										   SQLiteDatabase.CONFLICT_IGNORE);
				Log.e("RegionDaoSqlImpl", "db.update() returned " + updateCount);
				result = (updateCount == 1);
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
			result = db.delete(TABLE_NAME,
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
