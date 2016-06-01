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
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.CELL_EXIT_ARRAY_RESOURCE_NAME_INDEX;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.DOWN;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.Direction;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.EAST;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.ID_WHEN_RESOURCE_NOT_FOUND;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.UP;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WEST;

/**
 * Implementation of the {@link CellExitTypeDao} for managing {@link CellExitType} instances in a SQLite database.
 */
@Singleton
public class CellExitTypeTypeDaoSqlImpl extends BaseDaoSql implements CellExitTypeDao {
	// Cell exits table constants
	public static abstract class CellExitsTypesContract implements BaseColumns {
		public static final String TABLE_NAME   = "cell_exit_types";
		public static final String NAME         = "name";
		public static final String IS_SOLID     = "is_solid";
		public static final String USER_CREATED = "user_created";
	}
	private static       String[] cellExitTypesColumnNames     = {
			CellExitsTypesContract._ID,
			CellExitsTypesContract.NAME,
			CellExitsTypesContract.USER_CREATED,
			CellExitsTypesContract.IS_SOLID};
	public static final String    CREATE_TABLE_CELL_EXIT_TYPES =
		CREATE_TABLE + CellExitsTypesContract.TABLE_NAME + " (" +
			CellExitsTypesContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			CellExitsTypesContract.NAME + TEXT + NOT_NULL + COMMA +
			CellExitsTypesContract.USER_CREATED + BOOLEAN + NOT_NULL +
				CHECK + "(" + CellExitsTypesContract.USER_CREATED + IN + "(0,1))" + COMMA +
			CellExitsTypesContract.IS_SOLID + BOOLEAN + NOT_NULL +
				CHECK + "(" + CellExitsTypesContract.IS_SOLID + IN + "(0,1)));";

	// Cell exit bitmaps table constants
	public static abstract class CellExitTypeBitmapsContract implements BaseColumns {
		public static final String TABLE_NAME          = "cell_exit_type_bitmaps";
		public static final String CELL_EXIT_ID        = "cell_exit_id";
		public static final String CELL_EXIT_DIRECTION = "cell_exit_direction";
		public static final String BITMAP_DATA         = "bitmap_data";
	}
	public static final String    CREATE_TABLE_CELL_EXIT_TYPE_BITMAPS =
		CREATE_TABLE + CellExitTypeBitmapsContract.TABLE_NAME + "(" +
			CellExitTypeBitmapsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			CellExitTypeBitmapsContract.CELL_EXIT_ID + INTEGER + NOT_NULL + COMMA +
			CellExitTypeBitmapsContract.CELL_EXIT_DIRECTION + INTEGER + NOT_NULL + COMMA +
			CellExitTypeBitmapsContract.BITMAP_DATA + BLOB + NOT_NULL + COMMA +
			CONSTRAINT + "fk_bitmap_to_cell_exit" +
				FOREIGN_KEY+ "(" + CellExitTypeBitmapsContract.CELL_EXIT_ID + ")" +
				REFERENCES + CellExitsTypesContract.TABLE_NAME + "(" + CellExitsTypesContract._ID + ")" +
					ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_cell_exit_side_density" +
				UNIQUE + "(" + CellExitTypeBitmapsContract.CELL_EXIT_ID + COMMA +
					CellExitTypeBitmapsContract.CELL_EXIT_DIRECTION + "));";
	private static final int      ID_INDEX                            = 0;
	private static final int      NAME_INDEX                          = 1;
	private static final int      USER_CREATED_INDEX                  = 2;
	private static final int      SOLID_INDEX                         = 3;
	private static       String[] cellExitTypeBitmapsColumnNames      = {
			CellExitTypeBitmapsContract.CELL_EXIT_DIRECTION,
			CellExitTypeBitmapsContract.BITMAP_DATA};
	private static final int      DIRECTION_INDEX                     = 0;
	private static final int      BITMAP_DATA_INDEX                   = 1;

	// Member variables
	private SparseArray<CellExitType> cellExitTypeCache = new SparseArray<>();
	private Context          context;
	private SQLiteOpenHelper sqlHelper;

	/**
	 * Creates a new CellExitTypeDaoSqlImpl instance with the given Context and SQLiteOpenHelper.
	 *
	 * @param context  the Android context to use
	 * @param sqlHelper  an SQLiteOpenHelper instance
	 */
	@Inject
	public CellExitTypeTypeDaoSqlImpl(Context context, SQLiteOpenHelper sqlHelper) {
		this.context = context;
		this.sqlHelper = sqlHelper;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		int count = 0;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(CellExitsTypesContract.TABLE_NAME,
									 new String[]{"COUNT(*)"},
									 whereClause,
									 whereArgsList.toArray(whereArgs),
									 null,
									 null,
									 null,
									 null);
			cursor.moveToNext();
			count = cursor.getInt(0);
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
		return count;
	}

	@Override
	public CellExitType load(int id) {
		CellExitType cellExitType = cellExitTypeCache.get(id);
		if (cellExitType == null) {
			SQLiteDatabase db = sqlHelper.getReadableDatabase();
			boolean newTransaction = !db.inTransaction();
			if(newTransaction) {
				db.beginTransaction();
			}
			try {
				Cursor cursor = db.query(CellExitsTypesContract.TABLE_NAME,
										 cellExitTypesColumnNames,
										 CellExitsTypesContract._ID + "=?",
										 new String[]{String.valueOf(id)},
										 null,
										 null,
										 null);

				if (cursor.moveToFirst()) {
					cellExitType = createInstance(cursor);
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
		return cellExitType;
	}

	@Override
	public Collection<CellExitType> load(Collection<DaoFilter> filters) {
		int currentDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
		String[] bitmapSelectionArgs = new String[]{null, Integer.toString(currentDensityDpi)};

		if(filters != null && !filters.isEmpty()) {
			throw new UnsupportedOperationException("Loading a filtered collection of CellExit "
															+ "instances is not supported.");
		}

		if(cellExitTypeCache.size() == 0) {
			SQLiteDatabase db = sqlHelper.getReadableDatabase();
			boolean newTransaction = !db.inTransaction();
			if(newTransaction) {
				db.beginTransaction();
			}
			try {
				Cursor cursor = db.query(CellExitsTypesContract.TABLE_NAME,
										 cellExitTypesColumnNames,
										 null,
										 null,
										 null,
										 null,
										 null);
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					CellExitType cellExitType = createInstance(cursor);
					cellExitTypeCache.append(cellExitType.getId(), cellExitType);
					cursor.moveToNext();
				}
				cursor.close();

				for(int i = 0; i < cellExitTypeCache.size(); i++) {
					CellExitType cellExitType = cellExitTypeCache.valueAt(i);
					if(cellExitType.isUserCreated()) {
						bitmapSelectionArgs[0] = String.valueOf(cellExitType.getId());
						loadBitmaps(db, cellExitType, bitmapSelectionArgs, currentDensityDpi);
					}
					else {
						setAppBitmapForDeviceResolution(cellExitType);
					}
				}
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

		List<CellExitType> cellExitTypes = new ArrayList<>(cellExitTypeCache.size());
		for(int i=0; i < cellExitTypeCache.size(); i++) {
			cellExitTypes.add(cellExitTypeCache.valueAt(i));
		}
		return cellExitTypes;
	}

	@Override
	public boolean save(CellExitType cellExitType) {
		boolean result = false;
		ContentValues values = new ContentValues();

		values.put(CellExitsTypesContract.NAME, cellExitType.getName());
		values.put(CellExitsTypesContract.USER_CREATED, cellExitType.isUserCreated());
		values.put(CellExitsTypesContract.IS_SOLID, cellExitType.isSolid());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			if (cellExitType.getId() == -1) {
				cellExitType.setId((int) db.insert(CellExitsTypesContract.TABLE_NAME, null, values));
				result = (cellExitType.getId() == -1);
			}
			else {
				result = (db.update(CellExitsTypesContract.TABLE_NAME, values, CellExitsTypesContract._ID + " = ?",
									new String[]{Long.toString(cellExitType.getId())}) != 1);
			}
			values.clear();
			values.put(CellExitTypeBitmapsContract.CELL_EXIT_ID, cellExitType.getId());
			SparseArray<Bitmap> directionsBitmapsMap = cellExitType.getDirectionsBitmapsMap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			for (int i = 0; i < directionsBitmapsMap.size(); i++) {
				values.put(CellExitTypeBitmapsContract.CELL_EXIT_DIRECTION,
						   directionsBitmapsMap.keyAt(i));
				directionsBitmapsMap.valueAt(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
				values.put(CellExitTypeBitmapsContract.BITMAP_DATA, stream.toByteArray());
				result = (db.insertWithOnConflict(CellExitTypeBitmapsContract.TABLE_NAME, null,
												  values, SQLiteDatabase.CONFLICT_REPLACE) == -1);
				try {
					stream.close();
				}
				catch (IOException ex) {
					Log.e(this.getClass().getName(), "Error closing ByteArrayOutputStream", ex);
				}
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
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			result = db.delete(CellExitsTypesContract.TABLE_NAME,
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

	private CellExitType createInstance(Cursor cursor) {
		CellExitType cellExitType = new CellExitType();

		cellExitType.setId(cursor.getInt(ID_INDEX));
		cellExitType.setName(cursor.getString(NAME_INDEX));
		cellExitType.setUserCreated(cursor.getInt(USER_CREATED_INDEX) != 0);
		cellExitType.setSolid(cursor.getInt(SOLID_INDEX) != 0);

		return cellExitType;
	}

	private void loadBitmaps(SQLiteDatabase db, CellExitType cellExitType, String[] selectionArgs,
							 int currentDensity) {
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();

		Cursor cursor = db.query(CellExitTypeBitmapsContract.TABLE_NAME,
								 cellExitTypeBitmapsColumnNames,
								 CellExitTypeBitmapsContract.CELL_EXIT_ID + "= ?",
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
				cellExitType.addBitmapForDirection(direction, bitmap);
				cursor.moveToNext();
			}
			cursor.close();
	}

	@SuppressWarnings("ResourceType")
	private void setAppBitmapForDeviceResolution(CellExitType cellExitType) {
		Resources resources = context.getResources();
		int resId = resources.getIdentifier(cellExitType.getName(), null, null);
		TypedArray cellExitInfo = resources.obtainTypedArray(resId);
		try {
			cellExitType.setName(resources.getString(
					cellExitInfo.getResourceId(CELL_EXIT_ARRAY_RESOURCE_NAME_INDEX,
											   ID_WHEN_RESOURCE_NOT_FOUND)));
			BitmapDrawable drawable;
			if(cellExitInfo.getResourceId(UP, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(UP))) != null) {
					cellExitType.addBitmapForDirection(UP, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(NORTH, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(NORTH))) != null) {
					cellExitType.addBitmapForDirection(NORTH, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(WEST, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(WEST))) != null) {
					cellExitType.addBitmapForDirection(WEST, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(EAST, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(EAST))) != null) {
					cellExitType.addBitmapForDirection(EAST, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(SOUTH, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(SOUTH))) != null) {
					cellExitType.addBitmapForDirection(SOUTH, drawable.getBitmap());
				}
			}
			if(cellExitInfo.getResourceId(DOWN, ID_WHEN_RESOURCE_NOT_FOUND) != ID_WHEN_RESOURCE_NOT_FOUND) {
				if ((drawable = ((BitmapDrawable) cellExitInfo.getDrawable(DOWN))) != null) {
					cellExitType.addBitmapForDirection(DOWN, drawable.getBitmap());
				}
			}
		}
		finally {
			cellExitInfo.recycle();
		}
	}
}
