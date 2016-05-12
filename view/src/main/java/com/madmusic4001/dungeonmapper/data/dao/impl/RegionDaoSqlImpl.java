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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class RegionDaoSqlImpl extends BaseDaoSqlImpl implements RegionDao {
	private              String[] regionColumns     = {
			RegionsContract._ID,
			RegionsContract.NAME,
			RegionsContract.CREATE_TS,
			RegionsContract.MODIFIED_TS,
	};
	private static final int      REGION_ID_INDEX   = 0;
	private static final int      NAME_INDEX        = 1;
	private static final int      CREATE_TS_INDEX   = 2;
	private static final int      MODIFIED_TS_INDEX = 3;

	public RegionDaoSqlImpl(Context context, DungeonMapperSqlHelper helper) {
		super(context, helper);
	}

	@Override
	public int count() {
		throw new UnsupportedOperationException("count() is not implemented in RegionDaoSqlImpl");
	}

	@Override
	public Region load(String id) {
		throw new UnsupportedOperationException("load(String id) is not implemented in "
														+ "RegionDaoSqlImpl.");
	}

	@Override
	public Collection<Region> loadWithFilter(Region regionFilter) {
		Map<String, Region> regionNameMap = regionFilter.getParent().getRegionNameMap();
		Cursor cursor;

		SQLiteDatabase db = getReadableDatabase();
		cursor = db.query(RegionsContract.TABLE_NAME,
						  regionColumns,
						  RegionsContract.WORLD_ID + " = ?",
						  new String[]{Long.toString(regionFilter.getParent().getId())},
						  null,
						  null,
						  null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Region region = createRegionInstance(regionFilter.getParent(), cursor);
			regionNameMap.put(region.getName(), region);
			cursor.moveToNext();
		}
		cursor.close();
		return regionNameMap.values();
	}

	@Override
	public Collection<Region> loadAll() {
		throw new UnsupportedOperationException("Loading all instances is not implemented for "
														+ "Region entities");
	}

	@Override
	public void save(Region aRegion) {
		ContentValues values = new ContentValues();

		values.put(RegionsContract.WORLD_ID, aRegion.getParent().getId());
		values.put(RegionsContract.NAME, aRegion.getName());
		values.put(RegionsContract.CREATE_TS, aRegion.getCreateTs().getTimeInMillis());
		values.put(RegionsContract.MODIFIED_TS, Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase db = getWritableDatabase();
		if (aRegion.getId() == -1L) {
			aRegion.setId((int) db.insert(RegionsContract.TABLE_NAME, null, values));
			if (aRegion.getId() == -1L) {
				throw new DaoException(R.string.exception_regionNotSaved);
			}
		}
		else {
            if(db.updateWithOnConflict(
                    RegionsContract.TABLE_NAME, values, RegionsContract._ID + " = ?",
                    new String[] {Long.toString(aRegion.getId())},
                    SQLiteDatabase.CONFLICT_IGNORE) != 1) {
                throw new DaoException(R.string.exception_regionNotSaved);
            }
        }
    }

    @Override
    public void delete(Region aRegion) {
		SQLiteDatabase db = getWritableDatabase();
		if(db.delete(RegionsContract.TABLE_NAME,
					 RegionsContract._ID + "= ?",
					 new String[] {Long.toString(aRegion.getId())}) != 1){
			throw new DaoException(R.string.exception_regionNotRemoved, aRegion.getName());
		}
    }

	private Region createRegionInstance(World world, Cursor cursor) {
		Calendar cal;
        int regionId;

        Region region = new Region(cursor.getString(NAME_INDEX), world);
        regionId = cursor.getInt(REGION_ID_INDEX);
        region.setId(regionId);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(CREATE_TS_INDEX));
        region.setCreateTs(cal);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(MODIFIED_TS_INDEX));
        region.setModifiedTs(cal);

    	return region;
	}
}
