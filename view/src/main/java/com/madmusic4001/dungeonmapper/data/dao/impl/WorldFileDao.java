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

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.managers.CellExitManager;
import com.madmusic4001.dungeonmapper.controller.managers.TerrainManager;
import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
import com.madmusic4001.dungeonmapper.data.dao.DungeonMapperSqlHelper;
import com.madmusic4001.dungeonmapper.data.entity.AppSettings;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;
import com.madmusic4001.dungeonmapper.data.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.ALL_FILES_REGEX;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.APP_VERSION_ID;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.DOWN;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.NORTH;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.OriginLocation;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.PROJECTS_DIR;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WORLD_FILE_EXTENSION;

/**
 *
 */
@SuppressWarnings("unused")
@Singleton
public class WorldFileDao {
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	private TerrainManager         terrainManager;
	private CellExitManager        cellExitManager;
	private FileUtils              fileUtils;
	private DungeonMapperSqlHelper sqlHelper;

	@Inject
	public WorldFileDao(TerrainManager terrainManager,
						CellExitManager cellExitManager,
						FileUtils fileUtils,
						DungeonMapperSqlHelper sqlHelper) {
		this.terrainManager = terrainManager;
		this.cellExitManager = cellExitManager;
		this.fileUtils = fileUtils;
		this.sqlHelper = sqlHelper;
	}

	/**
	 * Loads a {@code World} from a file and saves it to the database.
	 *
	 * @param name  the name of the {@link World} to load. The file must named based on the world
	 *                name, nor null
	 * @param overwrite  true if an existing {@code World} with the same name should be
	 *                     overwritten with the data from the file or false if the current {@code
	 *                     World} should be kept as is
	 * @return a {@link World} instance with the given name
	 */
	public World loadFromFile(@NonNull final WorldManager manager, @NonNull String name,
							  boolean overwrite) {
		File file = null;
		DataInputStream stream = null;
		World newWorld = null;

		try {
			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(),
									 PROJECTS_DIR + File.separator + name,
									 name + WORLD_FILE_EXTENSION);
			if (AppSettings.useExternalStorageForWorlds() &&
					!fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_worldLoadError);
			}
			stream = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file.getAbsolutePath())));
			if ((newWorld = manager.getWorldWithName(name)) != null && !overwrite) {
				Log.d(this.getClass().getName(), "Returning existing world.");
				return newWorld;
			}
			sqlHelper.getWritableDatabase().beginTransaction();
			if (newWorld == null) {
				newWorld = manager.createNewWorld(name);
			}
			long appVersion = stream.readLong();
			if (appVersion > APP_VERSION_ID) {
				throw new DaoException(R.string.exception_versionMismatch);
			}
			newWorld.setOriginOffset(stream.readInt());
			@OriginLocation int tempInt = stream.readInt();
			newWorld.setOriginLocation(tempInt);
			newWorld.setRegionWidth(stream.readInt());
			newWorld.setRegionHeight(stream.readInt());
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(stream.readUTF()));
			newWorld.setCreateTs(cal);
			cal.setTime(dateFormat.parse(stream.readUTF()));
			newWorld.setModifiedTs(cal);
			manager.saveWorld(newWorld);
			// Read in all regions
			int regionCount = stream.readInt();
			for (int i = 0; i < regionCount; i++) {
				readRegion(stream, newWorld);
			}
			sqlHelper.getWritableDatabase().setTransactionSuccessful();
		}
		catch (IOException | ParseException ex) {
			throw new DaoException(R.string.exception_worldLoadError, name, ex);
		}
		finally {
			if(sqlHelper.getWritableDatabase().inTransaction()) {
				sqlHelper.getWritableDatabase().endTransaction();
			}
			if (stream != null) {
				try {
					stream.close();
				}
				catch (IOException ex) {
					Log.w(this.getClass().getName(), "Error occurred while closing stream for " +
							file.getAbsolutePath());
				}
			}
		}

		return newWorld;
	}

	/**
	 * Save a {@code World} instance to an external file.
	 *
	 * @param aWorld  the {@link World} instance to be saved to file, not null
	 * @return the name of the file the {@link World} was saved to.
	 */
	public String saveWorldToFile(@NonNull World aWorld) {
		DataOutputStream stream = null;
		File file = null;

		try {
			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(),
									 PROJECTS_DIR,
									 aWorld.getName() + WORLD_FILE_EXTENSION);
			if(AppSettings.useExternalStorageForWorlds() &&
					!fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_worldLoadError);
			}
			stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			stream.writeLong(APP_VERSION_ID);
			stream.writeInt(aWorld.getOriginOffset());
			stream.writeInt(aWorld.getOriginLocation());
			stream.writeInt(aWorld.getRegionWidth());
			stream.writeInt(aWorld.getRegionHeight());
			stream.writeUTF(dateFormat.format(aWorld.getCreateTs().getTime()));
			stream.writeUTF(dateFormat.format(aWorld.getModifiedTs().getTime()));
			stream.writeInt(aWorld.getRegionNameMap().size());
			for(Region aRegion : aWorld.getRegionNameMap().values()) {
				saveRegionToFile(stream, aRegion);
			}
			stream.flush();
			return file.getAbsolutePath();
		} catch (FileNotFoundException ex) {
			Log.e(this.getClass().getName(), file.getAbsolutePath() +
					" could not be opened for writing", ex);
			throw new DaoException(R.string.exception_worldNotSaved, file.getAbsolutePath(), ex);
		}
		catch (IOException ex) {
			Log.e(this.getClass().getName(), "Error occurred while writing " +
					file.getAbsolutePath(), ex);
			throw new DaoException(R.string.exception_worldNotSaved, aWorld.getName(), ex);
		}
		finally {
			if (stream != null) {
				try {
					stream.close();
				}
				catch(IOException ex){
					Log.w(this.getClass().getName(), "Error occurred while closing stream for " +
							file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Deletes a {@code World} stored in an external file.
	 *
	 * @param aWorld  the {@link World} to be deleted, not null.
	 */
	public void delete(@NonNull World aWorld) {
		Collection<File> files;
		String relativePath = PROJECTS_DIR + File.separator + aWorld.getName();
		String fileName = ALL_FILES_REGEX;

		files = fileUtils.getFiles(AppSettings.useExternalStorageForWorlds(),
								   relativePath, fileName);

		for(File aFile : files) {
			if(!aFile.delete()) {
				throw new DaoException(R.string.exception_worldNotRemoved, aWorld.getName());
			}
		}
		if(files.size() > 0) {
			if(!files.iterator().next().getParentFile().delete()) {
				throw new DaoException(R.string.exception_worldNotRemoved, aWorld.getName());
			}
		}
	}

	private void readRegion(DataInputStream stream, World parent) {
		String regionName = "UNKNOWN";
		Region newRegion;

		try {
			regionName = stream.readUTF();
			newRegion = parent.getRegionNameMap().get(regionName);
			if(newRegion == null) {
				newRegion = new Region(regionName, parent);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(stream.readUTF()));
			newRegion.setCreateTs(cal);
			cal.setTime(dateFormat.parse(stream.readUTF()));
			newRegion.setModifiedTs(cal);
			newRegion.setWidth(stream.readInt());
			newRegion.setHeight(stream.readInt());
			int numCells = stream.readInt();
			for(int i = 0; i < numCells; i++) {
				readCell(stream, newRegion, parent);
			}
		}
		catch (IOException | ParseException ex) {
			throw new DaoException(R.string.exception_regionLoadError, regionName,
								   parent.getName());
		}
	}

	private void readCell(DataInputStream stream, Region parent, World world) {
		Cell newCell = new Cell();
		int x = -1;
		int y = -1;

		try {
			newCell.setParent(parent);
			x = stream.readInt();
			newCell.setX(x);
			y = stream.readInt();
			newCell.setY(y);
			newCell.setSolid(stream.readBoolean());
			terrainManager.getTerrainWithName(stream.readUTF());
			parent.putCell(newCell);

			// Read cell links
			int numLinks = stream.readInt();
			for(int i = 0; i < numLinks; i++) {
				@DataConstants.Direction int linkDirection = stream.readInt();
				String linkRegionName = stream.readUTF();
				Region linkParent = world.getRegionNameMap().get(linkRegionName);
				if(linkParent == null) {
					linkParent = new Region(linkRegionName, world);
				}
				int linkX = stream.readInt();
				int linkY = stream.readInt();
				Cell linkCell = linkParent.getCell(linkX, linkY);
				if(linkCell == null) {
					linkCell = new Cell();
					linkCell.setX(linkX);
					linkCell.setY(linkY);
					linkParent.putCell(linkCell);
				}
				newCell.setLinkForDirection(linkDirection, linkCell);
			}

			// Read cell exits
			int numExits = stream.readInt();
			for(int i = 0; i < numExits; i++) {
				@DataConstants.Direction int exitDirection = stream.readInt();
				int cellExitId = stream.readInt();
				CellExit exitCell = cellExitManager.getCellExitWithId(cellExitId);
				newCell.setExitForDirection(exitDirection, exitCell);
			}
		}
		catch (IOException ex) {
			throw new DaoException(R.string.exception_cellLoadError, x, y, parent.getName(),
								   parent.getParent().getName());
		}
	}

	private void saveRegionToFile(DataOutputStream stream, Region region) {
		try {
			stream.writeUTF(region.getName());
			stream.writeUTF(dateFormat.format(region.getCreateTs().getTime()));
			stream.writeUTF(dateFormat.format(region.getModifiedTs().getTime()));
			stream.writeInt(region.getWidth());
			stream.writeInt(region.getHeight());
			stream.writeInt(region.getCells().size());
			for(Cell aCell : region.getCells()) {
				saveCellToFile(stream, aCell);
			}
		}
		catch (IOException ex) {
			throw new DaoException(R.string.exception_regionNotSaved, region.getName(),
								   region.getParent().getName());
		}
	}

	private void saveCellToFile(DataOutputStream stream, Cell cell) {
		try {
			stream.writeUTF(cell.getParent().getName());
			stream.writeInt(cell.getX());
			stream.writeInt(cell.getY());
			stream.writeBoolean(cell.isSolid());
			stream.writeUTF(cell.getTerrain().getName());

			// Save linked cell references
			stream.writeInt(cell.getNumLinks());
			for(@DataConstants.Direction int direction = NORTH; direction <= DOWN; direction++) {
				Cell linkCell = cell.getLinkForDirection(direction);
				if(linkCell != null) {
					stream.writeInt(direction);
					stream.writeUTF(linkCell.getParent().getName());
					stream.writeInt(linkCell.getX());
					stream.writeInt(linkCell.getY());
				}
			}

			// Save CellExit references
			stream.writeInt(cell.getNumExits());
			for(@DataConstants.Direction int direction = NORTH; direction <= DOWN; direction++) {
				CellExit exit = cell.getExitForDirection(direction);
				if(exit != null) {
					stream.writeInt(direction);
					stream.writeInt(exit.getId());
				}
			}
		}
		catch (IOException ex) {
			throw new DaoException(R.string.exception_cellNotSaved, cell.getX(), cell.getY(),
								   cell.getParent().getName(),
								   cell.getParent().getParent().getName());
		}
	}
}
