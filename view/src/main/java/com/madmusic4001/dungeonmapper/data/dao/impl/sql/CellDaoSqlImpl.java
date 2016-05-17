/**
 * Copyright (C) 2015 MadMusic4001
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
package com.madmusic4001.dungeonmapper.data.dao.impl.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.Direction;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTH;

/**
 * Implementation of the {@link CellDao} for managing {@link Cell} instances in a SQLite database.
 */
@Singleton
public class CellDaoSqlImpl extends BaseDaoSql implements CellDao {
	// Cell table constants
	public static abstract class CellsContract implements BaseColumns {
		public static final String TABLE_NAME   = "cells";
		public static final String QUALIFIED_ID = TABLE_NAME + "." + _ID;
		public static final String REGION_ID_COLUMN_NAME    = "region_id";
		public static final String TERRAIN_ID_COLUMN_NAME   = "terrain_id";
		public static final String IS_SOLID_COLUMN_NAME     = "is_solid";
		public static final String X_COORDINATE_COLUMN_NAME = "x_coordinate";
		public static final String Y_COORDINATE_COLUMN_NAME = "y_coordinate";
	}
	public static final String CREATE_TABLE_CELLS                 =
		CREATE_TABLE + CellsContract.TABLE_NAME + "(" +
			CellsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			CellsContract.REGION_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CellsContract.TERRAIN_ID_COLUMN_NAME + INTEGER + COMMA +
			CellsContract.IS_SOLID_COLUMN_NAME + BOOLEAN + NOT_NULL +
				CHECK + "(" + CellsContract.IS_SOLID_COLUMN_NAME + IN + "(0,1))" + COMMA +
			CellsContract.X_COORDINATE_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CellsContract.Y_COORDINATE_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CONSTRAINT + "fk_cell_to_map" +
				FOREIGN_KEY + "(" + CellsContract.REGION_ID_COLUMN_NAME + ")" +
				REFERENCES + RegionDaoSqlImpl.RegionsContract.TABLE_NAME + "(" + RegionDaoSqlImpl.RegionsContract._ID + ")" +
					ON + CASCADE + DELETE + COMMA +
			CONSTRAINT + "fk_cell_to_terrain" +
				FOREIGN_KEY + "(" + CellsContract.TERRAIN_ID_COLUMN_NAME + ")" +
				REFERENCES + TerrainDaoSqlImpl.TerrainsContract.TABLE_NAME + "(" + TerrainDaoSqlImpl.TerrainsContract._ID + ")" +
				ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_map_coordinates" +
				UNIQUE + "(" + CellsContract.REGION_ID_COLUMN_NAME + COMMA + CellsContract.X_COORDINATE_COLUMN_NAME + COMMA +
					CellsContract.Y_COORDINATE_COLUMN_NAME + "));";
	public static abstract class RegionCellExitsContract implements BaseColumns {
		public static final String TABLE_NAME   = "region_cell_exits";
		public static final String CELL_ID_COLUMN_NAME      = "cell_id";
		public static final String DIRECTION_COLUMN_NAME    = "direction";
		public static final String CELL_EXIT_ID_COLUMN_NAME = "cell_exit_id";
	}
	public static final String CREATE_TABLE_MAP_CELL_EXITS        =
		CREATE_TABLE + RegionCellExitsContract.TABLE_NAME + " (" +
			RegionCellExitsContract._ID + INTEGER + NOT_NULL + PRIMARY_KEY + COMMA +
			RegionCellExitsContract.CELL_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			RegionCellExitsContract.DIRECTION_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			RegionCellExitsContract.CELL_EXIT_ID_COLUMN_NAME + INTEGER + NOT_NULL + COMMA +
			CONSTRAINT + "fk_exit_to_cell" +
				FOREIGN_KEY + "(" + RegionCellExitsContract.CELL_ID_COLUMN_NAME + ")" +
				REFERENCES + CellsContract.TABLE_NAME + "(" + CellsContract._ID + ")" + ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "fk_cell_exit_to_cell" +
				FOREIGN_KEY + "(" + RegionCellExitsContract.CELL_EXIT_ID_COLUMN_NAME + ")" +
				REFERENCES + CellExitTypeTypeDaoSqlImpl.CellExitsTypesContract.TABLE_NAME + "(" +
					CellExitTypeTypeDaoSqlImpl.CellExitsTypesContract._ID + ")" + ON + DELETE + CASCADE + COMMA +
			CONSTRAINT + "unique_exit_direction" +
				UNIQUE + "(" + RegionCellExitsContract.CELL_ID_COLUMN_NAME + COMMA +
					RegionCellExitsContract.DIRECTION_COLUMN_NAME + "));";
	private static final int      CELL_ID_INDEX      = 0;
	private static final int      TERRAIN_ID_INDEX   = 1;
	private static final int      IS_SOLID_INDEX     = 2;
	private static final int      X_COORDINATE_INDEX = 3;
	private static final int      Y_COORDINATE_INDEX = 4;
	private static final int      DIRECTION_INDEX    = 5;
	private static final int      EXIT_ID_INDEX      = 6;
	private static       String[] projection         = {
			CellsContract.QUALIFIED_ID,
			CellsContract.TERRAIN_ID_COLUMN_NAME,
			CellsContract.IS_SOLID_COLUMN_NAME,
			CellsContract.X_COORDINATE_COLUMN_NAME,
			CellsContract.Y_COORDINATE_COLUMN_NAME,
			RegionCellExitsContract.DIRECTION_COLUMN_NAME,
			RegionCellExitsContract.CELL_EXIT_ID_COLUMN_NAME
	};
	private SQLiteOpenHelper sqlHelper;
	private CellExitTypeDao  cellExitTypeDao;
	private TerrainDao       terrainDao;

	/**
	 * Creates a new CellDaoSqlImpl instance.
	 *
	 * @param sqlHelper  an SQLiteOpenHelper instance
	 * @param cellExitTypeDao  a CellExitDao instance
	 */
	@Inject
	public CellDaoSqlImpl(DungeonMapperSqlHelper sqlHelper, CellExitTypeDao cellExitTypeDao,
						  TerrainDao terrainDao) {
		this.sqlHelper = sqlHelper;
		this.cellExitTypeDao = cellExitTypeDao;
		this.terrainDao = terrainDao;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		throw new UnsupportedOperationException("count() not implemented in CellDaoSqlImpl");
	}

	@Override
	public Cell load(int id) {
		throw new UnsupportedOperationException("load(String id) not implemented in "
														+ "CellDaoSqlImpl");
	}

	@Override
	public Collection<Cell> load(Collection<DaoFilter> filters) {
		Collection<Cell> cells = null;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(CellsContract.TABLE_NAME,
									 projection,
									 whereClause,
									 whereArgsList.toArray(whereArgs),
									 null,
									 null,
									 null);
			if(cursor != null && !cursor.isClosed()) {
				cells = new HashSet<>();
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Cell aCell = createInstance(cursor, null);
					cells.add(aCell);
					cursor.moveToNext();
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		}
		finally {
			db.endTransaction();
		}
		return cells;
	}

	@Override
	public boolean save(Cell cell) {
		boolean result = false;

		List<String> whereArgsList = new ArrayList<>();
		Collection<DaoFilter> filters = new ArrayList<>();
		filters.add(createFilter(DaoFilter.Operator.EQUALS,
								 CellsContract.REGION_ID_COLUMN_NAME,
								 Integer.valueOf(cell.getParent().getId()).toString()));
		filters.add(createFilter(DaoFilter.Operator.EQUALS,
								 CellsContract.X_COORDINATE_COLUMN_NAME,
								 Integer.valueOf(cell.getX()).toString()));
		filters.add(createFilter(DaoFilter.Operator.EQUALS,
								 CellsContract.Y_COORDINATE_COLUMN_NAME,
								 Integer.valueOf(cell.getY()).toString()));
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		ContentValues values = new ContentValues();
		values.put(CellsContract.REGION_ID_COLUMN_NAME, cell.getParent().getId());
		if (cell.getTerrain() != null) {
			values.put(CellsContract.TERRAIN_ID_COLUMN_NAME, cell.getTerrain().getId());
		}
		else {
			values.putNull(CellsContract.TERRAIN_ID_COLUMN_NAME);
		}
		values.put(CellsContract.X_COORDINATE_COLUMN_NAME, cell.getX());
		values.put(CellsContract.Y_COORDINATE_COLUMN_NAME, cell.getY());
		values.put(CellsContract.IS_SOLID_COLUMN_NAME, cell.isSolid());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if(cell.getId() == -1) {
				cell.setId((int) db.insertOrThrow(CellsContract.TABLE_NAME, null, values));
				result = (cell.getId() > DataConstants.UNINITIALIZED);
			}
			else {
				result = (db.update(CellsContract.TABLE_NAME,
									values,
						  			whereClause,
									whereArgsList.toArray(whereArgs)) != 1);
			}
			persistCellExits(cell, db);
			db.setTransactionSuccessful();
		}
		catch(SQLiteConstraintException ex) {
			throw new DaoException(ex, R.string.exception_cellNotSaved, cell.getX(), cell.getY());
		}
		finally {
			db.endTransaction();
		}
		return result;
	}

	@Override
	public int delete(Collection<DaoFilter> filters) {
		int result = 0;
		List<String> whereArgsList = new ArrayList<>();
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		database.beginTransaction();
		try {
			result = database.delete(CellsContract.TABLE_NAME,
									  whereClause,
									  whereArgsList.toArray(whereArgs));
			if(result > 0) {
				database.setTransactionSuccessful();
			}
		}
		finally {
			database.endTransaction();
		}
		return result;
	}

	private Cell createInstance(Cursor cursor, Region region) {
		int cellId;

		Cell cell = new Cell();
		cell.setParent(region);
		cellId = cursor.getInt(CELL_ID_INDEX);
		cell.setId(cellId);
		cell.setTerrain(terrainDao.load(cursor.getInt(TERRAIN_ID_INDEX)));
		cell.setSolid(cursor.getInt(IS_SOLID_INDEX) != 0);
		cell.setX(cursor.getInt(X_COORDINATE_INDEX));
		cell.setY(cursor.getInt(Y_COORDINATE_INDEX));

		while(!cursor.isAfterLast() && cursor.getLong(CELL_ID_INDEX) == cellId &&
				!cursor.isNull(DIRECTION_INDEX)) {
			@Direction int tempDir = cursor.getInt(DIRECTION_INDEX);
			cell.setExitForDirection(tempDir, cellExitTypeDao.load(cursor.getInt(EXIT_ID_INDEX)));
			cursor.moveToNext();
		}
		return cell;
	}

	private boolean persistCellExits(Cell cell, SQLiteDatabase db) {
		boolean result = true;
		ContentValues values = new ContentValues();

		db.delete(RegionCellExitsContract.TABLE_NAME,
				  RegionCellExitsContract.CELL_ID_COLUMN_NAME + " = ?",
				  new String[]{Long.toString(cell.getId())});
		for(@Direction int direction = NORTH; direction <= SOUTH && result;
			direction++) {
			CellExitType exit = cell.getExitForDirection(direction);
			if(exit != null) {
				values.put(RegionCellExitsContract.CELL_ID_COLUMN_NAME, cell.getId());
				values.put(RegionCellExitsContract.DIRECTION_COLUMN_NAME, direction);
				values.put(RegionCellExitsContract.CELL_EXIT_ID_COLUMN_NAME, exit.getId());
				result = (db.insert(RegionCellExitsContract.TABLE_NAME, null, values) >= DataConstants.UNINITIALIZED);
			}
		}
		return result;
	}
}
