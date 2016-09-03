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

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.schemas.CellSchema;
import com.madmusic4001.dungeonmapper.data.dao.schemas.RegionCellExitSchema;
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
public class CellDaoSqlImpl extends BaseDaoSql implements CellDao, CellSchema {
	private SQLiteOpenHelper sqlHelper;
	private CellExitTypeDao  cellExitTypeDao;
	private TerrainDao       terrainDao;
	private FilterCreator    filterCreator;

	/**
	 * Creates a new CellDaoSqlImpl instance.
	 *
	 * @param sqlHelper  an SQLiteOpenHelper instance
	 * @param cellExitTypeDao  a CellExitDao instance
	 */
	@Inject
	public CellDaoSqlImpl(SQLiteOpenHelper sqlHelper, CellExitTypeDao cellExitTypeDao,
						  TerrainDao terrainDao, FilterCreator filterCreator) {
		this.sqlHelper = sqlHelper;
		this.cellExitTypeDao = cellExitTypeDao;
		this.terrainDao = terrainDao;
		this.filterCreator = filterCreator;
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
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			Cursor cursor = db.query(TABLE_NAME,
									 RegionCellExitSchema.PROJECTION,
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
		return cells;
	}

	@Override
	public boolean save(Cell cell) {
		boolean result = false;

		List<String> whereArgsList = new ArrayList<>();
		Collection<DaoFilter> filters = new ArrayList<>();
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
								 REGION_ID_COLUMN_NAME,
								 Integer.valueOf(cell.getParent().getId()).toString()));
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
								 X_COORDINATE_COLUMN_NAME,
								 Integer.valueOf(cell.getX()).toString()));
		filters.add(filterCreator.createDaoFilter(DaoFilter.Operator.EQUALS,
								 Y_COORDINATE_COLUMN_NAME,
								 Integer.valueOf(cell.getY()).toString()));
		String whereClause = buildWhereArgs(filters, whereArgsList);
		String[] whereArgs = new String[whereArgsList.size()];

		ContentValues values = new ContentValues();
		values.put(REGION_ID_COLUMN_NAME, cell.getParent().getId());
		if (cell.getTerrain() != null) {
			values.put(TERRAIN_ID_COLUMN_NAME, cell.getTerrain().getId());
		}
		else {
			values.putNull(TERRAIN_ID_COLUMN_NAME);
		}
		values.put(X_COORDINATE_COLUMN_NAME, cell.getX());
		values.put(Y_COORDINATE_COLUMN_NAME, cell.getY());
		values.put(IS_SOLID_COLUMN_NAME, cell.isSolid());

		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		boolean newTransaction = !db.inTransaction();
		if(newTransaction) {
			db.beginTransaction();
		}
		try {
			if(cell.getId() == -1) {
				cell.setId((int) db.insertOrThrow(TABLE_NAME, null, values));
				result = (cell.getId() > DataConstants.UNINITIALIZED);
			}
			else {
				result = (db.update(TABLE_NAME,
									values,
						  			whereClause,
									whereArgsList.toArray(whereArgs)) != 1);
			}
			persistCellExits(cell, db);
			if(newTransaction) {
				db.setTransactionSuccessful();
			}
		}
		catch(SQLiteConstraintException ex) {
			throw new DaoException(ex, R.string.exception_cellNotSaved, cell.getX(), cell.getY());
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
			result = db.delete(TABLE_NAME,
									  whereClause,
									  whereArgsList.toArray(whereArgs));
			if(result > 0 && newTransaction) {
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

	private Cell createInstance(Cursor cursor, Region region) {
		int cellId;

		Cell cell = new Cell();
		cell.setParent(region);
		cellId = cursor.getInt(RegionCellExitSchema.CELL_ID_INDEX);
		cell.setId(cellId);
		cell.setTerrain(terrainDao.load(cursor.getInt(RegionCellExitSchema.TERRAIN_ID_INDEX)));
		cell.setSolid(cursor.getInt(RegionCellExitSchema.IS_SOLID_INDEX) != 0);
		cell.setX(cursor.getInt(RegionCellExitSchema.X_COORDINATE_INDEX));
		cell.setY(cursor.getInt(RegionCellExitSchema.Y_COORDINATE_INDEX));

		while(!cursor.isAfterLast() && cursor.getLong(RegionCellExitSchema.CELL_ID_INDEX) == cellId &&
				!cursor.isNull(RegionCellExitSchema.DIRECTION_INDEX)) {
			@Direction int tempDir = cursor.getInt(RegionCellExitSchema.DIRECTION_INDEX);
			cell.setExitForDirection(tempDir, cellExitTypeDao.load(cursor.getInt(RegionCellExitSchema.EXIT_ID_INDEX)));
			cursor.moveToNext();
		}
		return cell;
	}

	private boolean persistCellExits(Cell cell, SQLiteDatabase db) {
		boolean result = true;
		ContentValues values = new ContentValues();

		db.delete(RegionCellExitSchema.TABLE_NAME,
				  RegionCellExitSchema.CELL_ID_COLUMN_NAME + " = ?",
				  new String[]{Long.toString(cell.getId())});
		for(@Direction int direction = NORTH; direction <= SOUTH && result;
			direction++) {
			CellExitType exit = cell.getExitForDirection(direction);
			if(exit != null) {
				values.put(RegionCellExitSchema.CELL_ID_COLUMN_NAME, cell.getId());
				values.put(RegionCellExitSchema.DIRECTION_COLUMN_NAME, direction);
				values.put(RegionCellExitSchema.CELL_EXIT_ID_COLUMN_NAME, exit.getId());
				result = (db.insert(RegionCellExitSchema.TABLE_NAME, null, values) >= DataConstants.UNINITIALIZED);
			}
		}
		return result;
	}
}
