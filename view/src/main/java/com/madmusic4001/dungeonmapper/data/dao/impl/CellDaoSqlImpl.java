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
package com.madmusic4001.dungeonmapper.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.CellExitDao;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.Direction;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.SOUTH;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/2/2015.
 */
@Singleton
public class CellDaoSqlImpl extends BaseDaoSqlImpl implements CellDao {
	private TerrainDao  terrainDao;
	private CellExitDao cellExitDao;
	private TerrainManager terrainManager;

	private static final int      CELL_ID_INDEX      = 0;
	private static final int      TERRAIN_ID_INDEX   = 1;
	private static final int      IS_SOLID_INDEX     = 2;
	private static final int      X_COORDINATE_INDEX = 3;
	private static final int      Y_COORDINATE_INDEX = 4;
	private static final int      DIRECTION_INDEX    = 5;
	private static final int      EXIT_ID_INDEX      = 6;
	private static       String[] projection         = {
			CellDao.CellsContract.QUALIFIED_ID,
			CellDao.CellsContract.TERRAIN_ID,
			CellDao.CellsContract.IS_SOLID,
			CellDao.CellsContract.X_COORDINATE,
			CellDao.CellsContract.Y_COORDINATE,
			RegionCellExitsContract.DIRECTION,
			RegionCellExitsContract.CELL_EXIT_ID
	};

	public CellDaoSqlImpl(Context context, DungeonMapperSqlHelper helper, TerrainDao terrainDao, CellExitDao cellExitDao,
						  TerrainManager terrainManager) {
		super(context, helper);
		this.terrainDao = terrainDao;
		this.cellExitDao = cellExitDao;
		this.terrainManager = terrainManager;
	}

	@Override
	public int count() {
		throw new UnsupportedOperationException("count() not implemented in CellDaoSqlImpl");
	}

	@Override
	public Cell load(String id) {
		throw new UnsupportedOperationException("load(String id) not implemented in "
														+ "CellDaoSqlImpl");
	}

	@Override
	public Collection<Cell> loadWithFilter(Cell filter) {
		List<Cell> cells = new ArrayList<>();
		String CELLS_FROM_CLAUSE = CellDao.CellsContract.TABLE_NAME +
				" LEFT OUTER JOIN " + RegionCellExitsContract.TABLE_NAME + " ON (" +
				CellDao.CellsContract.QUALIFIED_ID + " = " +
				RegionCellExitsContract.CELL_ID + ")";
		Cursor cursor;
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(CELLS_FROM_CLAUSE);
		SQLiteDatabase db = getReadableDatabase();
		cursor = builder.query(db,
							   projection,
							   CellDao.CellsContract.REGION_ID + " = ?",
							   new String[]{Long.toString(filter.getParent().getId())},
							   null,
							   null,
							   CellDao.CellsContract.QUALIFIED_ID + ", " +
									   RegionCellExitsContract.DIRECTION
		);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			cells.add(createCellInstance(cursor, filter.getParent()));
		}
		cursor.close();
		return cells;
	}

	@Override
	public Collection<Cell> loadAll() {
		return null;
	}

	@Override
	public void save(Cell cell) {
		ContentValues values = new ContentValues();

		values.put(CellDao.CellsContract.REGION_ID, cell.getParent().getId());
		if (cell.getTerrain() != null) {
			values.put(CellDao.CellsContract.TERRAIN_ID, cell.getTerrain().getId());
		}
		else {
			values.putNull(CellDao.CellsContract.TERRAIN_ID);
		}
		values.put(CellDao.CellsContract.X_COORDINATE, cell.getX());
		values.put(CellDao.CellsContract.Y_COORDINATE, cell.getY());
		values.put(CellDao.CellsContract.IS_SOLID, cell.isSolid());
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.beginTransaction();
			if(cell.getId() == -1) {
				cell.setId((int) db.insertOrThrow(CellDao.CellsContract.TABLE_NAME, null, values));
			}
			else if (db.update(CellDao.CellsContract.TABLE_NAME, values,
							   CellDao.CellsContract.REGION_ID + " = ? AND " +
									   CellDao.CellsContract.X_COORDINATE + " = ? AND " +
									   CellDao.CellsContract.Y_COORDINATE + " = ?",
							   new String[]{Long.toString(cell.getParent().getId()),
									   Integer.toString(cell.getX()),
									   Integer.toString(cell.getY())}) != 1) {
				throw new DaoException(R.string.exception_cellNotSaved);
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
	}

	@Override
	public void delete(Cell entity) {
		throw new UnsupportedOperationException("delete() not implemented in CellDaoSqlImpl");
	}

	private Cell createCellInstance(Cursor cursor, Region region) {
		int cellId;

		Cell cell = new Cell();
		cell.setParent(region);
		cellId = cursor.getInt(CELL_ID_INDEX);
		cell.setId(cellId);
		cell.setTerrain(terrainManager.getTerrainWithId(cursor.getLong(TERRAIN_ID_INDEX)));
		cell.setSolid(cursor.getInt(IS_SOLID_INDEX) != 0);
		cell.setX(cursor.getInt(X_COORDINATE_INDEX));
		cell.setY(cursor.getInt(Y_COORDINATE_INDEX));

		while(!cursor.isAfterLast() && cursor.getLong(CELL_ID_INDEX) == cellId &&
				!cursor.isNull(DIRECTION_INDEX)) {
			@Direction int tempDir = cursor.getInt(DIRECTION_INDEX);
			cell.setExitForDirection(tempDir, cellExitDao.load(cursor.getString(EXIT_ID_INDEX)));
			cursor.moveToNext();
		}
		return cell;
	}

	private void persistCellExits(Cell cell, SQLiteDatabase db) {
		ContentValues values = new ContentValues();

		db.delete(RegionCellExitsContract.TABLE_NAME,
				  RegionCellExitsContract.CELL_ID + " = ?",
				  new String[]{Long.toString(cell.getId())});
		for(@Direction int direction = NORTH; direction <= SOUTH;
			direction++) {
			CellExit exit = cell.getExitForDirection(direction);
			if(exit != null) {
				values.put(RegionCellExitsContract.CELL_ID, cell.getId());
				values.put(RegionCellExitsContract.DIRECTION, direction);
				values.put(RegionCellExitsContract.CELL_EXIT_ID, exit.getId());
				db.insert(RegionCellExitsContract.TABLE_NAME, null, values);
			}
		}
	}
}
