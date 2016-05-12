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
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;

/**
 *
 */
@Singleton
public class WorldDaoSqlImpl extends BaseDaoSqlImpl implements WorldDao {
	private static       String[] columnNames           = {
			WorldDao.WorldsContract._ID,
			WorldDao.WorldsContract.NAME,
			WorldDao.WorldsContract.REGION_ORIGIN_OFFSET,
			WorldDao.WorldsContract.REGION_ORIGIN_POSITION,
			WorldDao.WorldsContract.REGION_WIDTH,
			WorldDao.WorldsContract.REGION_HEIGHT,
			WorldDao.WorldsContract.CREATE_TS,
			WorldDao.WorldsContract.MODIFIED_TS};
	private static final int      ID_INDEX              = 0;
	private static final int      NAME_INDEX            = 1;
	private static final int      ORIGIN_OFFSET_INDEX   = 2;
	private static final int      ORIGIN_POSITION_INDEX = 3;
	private static final int      MAP_WIDTH_INDEX       = 4;
	private static final int      MAP_HEIGHT_INDEX      = 5;
	private static final int      CREATE_TS_INDEX       = 6;
	private static final int      MODIFIED_TS_INDEX     = 7;

	public WorldDaoSqlImpl(Context context, DungeonMapperSqlHelper helper) {
		super(context, helper);
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public World load(String name) {
		World theWorld = null;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(WorldDao.WorldsContract.TABLE_NAME,
								 columnNames,
								 WorldDao.WorldsContract.NAME + "= ?",
								 new String[]{name},
								 null,
								 null,
								 null);

		if (cursor.moveToFirst()) {
			theWorld = createInstance(cursor);
		}
		cursor.close();
		return theWorld;
	}

	@Override
	public Collection<World> loadWithFilter(World filter) {
		return null;
	}

	@Override
	public Collection<World> loadAll() {
		List<World> worldList = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(WorldDao.WorldsContract.TABLE_NAME,
								 columnNames,
								 null,
								 null,
								 null,
								 null,
								 null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			World world = createInstance(cursor);
			worldList.add(world);
			cursor.moveToNext();
		}
		cursor.close();
		return worldList;
	}

	@Override
	public void save(World aWorld) {
		ContentValues values = new ContentValues();

		values.put(WorldDao.WorldsContract.NAME, aWorld.getName());
		values.put(WorldDao.WorldsContract.REGION_ORIGIN_OFFSET, aWorld.getOriginOffset());
		values.put(WorldDao.WorldsContract.REGION_ORIGIN_POSITION,
				   aWorld.getOriginLocation());
		values.put(WorldDao.WorldsContract.REGION_WIDTH, aWorld.getRegionWidth());
		values.put(WorldDao.WorldsContract.REGION_HEIGHT, aWorld.getRegionHeight());
		values.put(WorldDao.WorldsContract.CREATE_TS,
				   aWorld.getCreateTs().getTimeInMillis());
		values.put(WorldDao.WorldsContract.MODIFIED_TS,
				   Calendar.getInstance().getTimeInMillis());

		SQLiteDatabase db = getWritableDatabase();
		if(aWorld.getId() == -1) {
			aWorld.setId((int)db.insert(WorldDao.WorldsContract.TABLE_NAME, null, values));
			if (aWorld.getId() == -1) {
				throw new DaoException(R.string.exception_worldNotSaved, aWorld.getName());
			}
		}
		else {
			try {
				if (db.update(WorldDao.WorldsContract.TABLE_NAME, values,
							  WorldDao.WorldsContract._ID + "= ?",
							  new String[]{Long.toString(aWorld.getId())}) != 1) {
					throw new DaoException(R.string.exception_worldNotSaved, aWorld.getName());
				}
			}
			catch(SQLiteConstraintException ex) {
				throw new DaoException(R.string.exception_worldNotSaved, aWorld.getName(), ex);
			}
		}
	}

	@Override
	public void delete(World aWorld) {
		SQLiteDatabase db = getWritableDatabase();
		if(db.delete(WorldDao.WorldsContract.TABLE_NAME,
					 RegionDao.RegionsContract._ID + "= ?",
					 new String[]{Long.toString(aWorld.getId())}) != 1){
			throw new DaoException(R.string.exception_regionNotRemoved, aWorld.getName());
		}
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
