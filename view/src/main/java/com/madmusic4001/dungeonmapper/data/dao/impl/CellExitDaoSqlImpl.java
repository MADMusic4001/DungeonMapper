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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.CellExitDao;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.*;

/**
 *
 */
@Singleton
public class CellExitDaoSqlImpl extends BaseDaoSqlImpl implements CellExitDao {
	private static       String[]                   cellExitsColumnNames       = {
			CellExitsContract._ID,
			CellExitsContract.NAME,
			CellExitsContract.USER_CREATED,
			CellExitsContract.IS_SOLID};
	private static final int                        ID_INDEX                   = 0;
	private static final int                        NAME_INDEX                 = 1;
	private static final int                        USER_CREATED_INDEX         = 2;
	private static final int                        SOLID_INDEX                = 3;
	private static       String[]                   cellExitBitmapsColumnNames = {
			CellExitBitmapsContract.CELL_EXIT_DIRECTION,
			CellExitBitmapsContract.BITMAP_DATA};
	private static final int                        DIRECTION_INDEX            = 0;
	private static final int                        BITMAP_DATA_INDEX          = 1;
	private              LruCache<String, CellExit> cellExitCache              = new LruCache<>(10);

	public CellExitDaoSqlImpl(Context context, DungeonMapperSqlHelper helper) {
		super(context, helper);
	}

	@Override
	public int count() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT COUNT(*) AS numExits FROM " + CellExitsContract.TABLE_NAME,
				null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	@Override
	public CellExit load(String name) {
		CellExit cellExit = cellExitCache.get(name);
		if (cellExit == null) {

			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query(CellExitsContract.TABLE_NAME,
									 cellExitsColumnNames,
									 CellExitsContract.NAME + "= ?",
									 new String[]{name},
									 null,
									 null,
									 null);

			if (cursor.moveToFirst()) {
				cellExit = createInstance(cursor);
			}
			cursor.close();
		}
		return cellExit;
	}

	@Override
	public Collection<CellExit> loadWithFilter(CellExit filter) {
		throw new UnsupportedOperationException("Loading a filtered collection of CellExit "
														+ "instances is not supported.");
	}

	@Override
	public Collection<CellExit> loadAll() {
		List<CellExit> cellExits = new ArrayList<>();

		int currentDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
		String[] bitmapSelectionArgs = new String[]{null, Integer.toString(currentDensityDpi)};
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(CellExitsContract.TABLE_NAME,
								 cellExitsColumnNames,
								 null,
								 null,
								 null,
								 null,
								 null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CellExit cellExit = createInstance(cursor);
			cellExits.add(cellExit);
			cursor.moveToNext();
		}
		cursor.close();

		for(CellExit cellExit : cellExits) {
			if(cellExit.isUserCreated()) {
				bitmapSelectionArgs[0] = Long.toString(cellExit.getId());
				loadBitmaps(db, cellExit, bitmapSelectionArgs,currentDensityDpi);
			}
			else {
				setAppBitmapForDeviceResolution(cellExit);
			}
		}
		return cellExits;
	}

	@Override
	public void save(CellExit cellExit) {
		ContentValues values = new ContentValues();

		values.put(CellExitsContract.NAME, cellExit.getName());
		values.put(CellExitsContract.USER_CREATED, cellExit.isUserCreated());
		values.put(CellExitsContract.IS_SOLID, cellExit.isSolid());

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		if(cellExit.getId() == -1) {
			cellExit.setId((int)db.insert(CellExitsContract.TABLE_NAME, null, values));
			if(cellExit.getId() == -1) {
				throw new DaoException(R.string.exception_cellExitNotSaved);
			}
		}
		else {
			if(db.update(CellExitsContract.TABLE_NAME, values, CellExitsContract._ID + " = ?",
						 new String[] {Long.toString(cellExit.getId())}) != 1) {
				throw new DaoException(R.string.exception_cellExitNotSaved);
			}
		}
		values.clear();
		values.put(CellExitBitmapsContract.CELL_EXIT_ID, cellExit.getId());
		SparseArray<Bitmap> directionsBitmapsMap = cellExit.getDirectionsBitmapsMap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for(int i = 0; i < directionsBitmapsMap.size(); i++) {
			values.put(CellExitBitmapsContract.CELL_EXIT_DIRECTION,
					   directionsBitmapsMap.keyAt(i));
			directionsBitmapsMap.valueAt(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
			values.put(CellExitBitmapsContract.BITMAP_DATA, stream.toByteArray());
			if(db.insertWithOnConflict(CellExitBitmapsContract.TABLE_NAME, null,
									   values, SQLiteDatabase.CONFLICT_REPLACE) == -1) {
				throw new DaoException(R.string.exception_cellExitNotSaved);
			}
			try {
				stream.close();
			} catch (IOException ex) {
				Log.e(this.getClass().getName(), "Error closing ByteArrayOutputStream", ex);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void delete(CellExit cellExit) {
		SQLiteDatabase db = getWritableDatabase();

		if(db.delete(CellExitsContract.TABLE_NAME,
					 CellExitsContract._ID + " = ?",
					 new String[] {Long.toString(cellExit.getId())}) == 0) {
			throw new DaoException(R.string.exception_cellExitNotRemoved);
		}
	}

	private CellExit createInstance(Cursor cursor) {
		CellExit cellExit = new CellExit();

		cellExit.setId(cursor.getInt(ID_INDEX));
		cellExit.setName(cursor.getString(NAME_INDEX));
		cellExit.setUserCreated(cursor.getInt(USER_CREATED_INDEX) != 0);
		cellExit.setSolid(cursor.getInt(SOLID_INDEX) != 0);

		return cellExit;
	}

	private void loadBitmaps(SQLiteDatabase db, CellExit cellExit, String[] selectionArgs,
                             int currentDensity) {
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();

		Cursor cursor = db.query(CellExitBitmapsContract.TABLE_NAME,
				cellExitBitmapsColumnNames,
				CellExitBitmapsContract.CELL_EXIT_ID + "= ?",
                selectionArgs,
				null,
				null,
				null);

			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
                @Direction int direction = cursor.getInt(DIRECTION_INDEX);
				byte[] bitmapData = cursor.getBlob(BITMAP_DATA_INDEX);
				options.inDensity = currentDensity;
				options.inTargetDensity = currentDensity;
				options.inScaled = false;
				bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, options);
				cellExit.addBitmapForDirection(direction, bitmap);
				cursor.moveToNext();
			}
			cursor.close();
	}

	private void setAppBitmapForDeviceResolution(CellExit cellExit) {
		Resources resources = context.getResources();
		int resId = resources.getIdentifier(cellExit.getName(), null, null);
		TypedArray cellExitInfo = resources.obtainTypedArray(resId);
		try {
			cellExit.setName(resources.getString(cellExitInfo.getResourceId(CELL_EXIT_ARRAY_RESOURCE_NAME_INDEX,
												ID_WHEN_RESOURCE_NOT_FOUND)));
			BitmapDrawable drawable;
			if(cellExitInfo.getResourceId(UP, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				Log.d(this.getClass().getName(), "Resource id = " + cellExitInfo.getResourceId(UP, ID_WHEN_RESOURCE_NOT_FOUND));
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(UP))) != null) {
					cellExit.addBitmapForDirection(UP, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(NORTH, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(NORTH))) != null) {
					cellExit.addBitmapForDirection(NORTH, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(WEST, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(WEST))) != null) {
					cellExit.addBitmapForDirection(WEST, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(EAST, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(EAST))) != null) {
					cellExit.addBitmapForDirection(EAST, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(SOUTH, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(SOUTH))) != null) {
					cellExit.addBitmapForDirection(SOUTH, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(DOWN, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(DOWN))) != null) {
					cellExit.addBitmapForDirection(DOWN, drawable.getBitmap());
				}
			}
		}
		finally {
			cellExitInfo.recycle();
		}
	}
}
