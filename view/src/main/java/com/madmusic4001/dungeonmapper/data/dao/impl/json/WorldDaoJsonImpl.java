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

package com.madmusic4001.dungeonmapper.data.dao.impl.json;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.AppSettings;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
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
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.APP_VERSION_ID;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WORLDS_DIR;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.WORLD_FILE_EXTENSION;

/**
 *
 */
@SuppressWarnings("unused")
@Singleton
public class WorldDaoJsonImpl implements WorldDao {
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
//	private TerrainDao             terrainDao;
//	private CellExitTypeDao		   cellExitTypeDao;
	private FileUtils              fileUtils;
//	private DungeonMapperSqlHelper sqlHelper;
//	private EventBus			   eventBus;
	private String saveFileName;

	@Inject
	public WorldDaoJsonImpl(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	@Override
	public int count(Collection<DaoFilter> filters) {
		return 0;
	}

	@Override
	public World load(int id) {
		File file = null;
		InputStreamReader stream = null;
		VersionedWorld versionedWorld = null;

		try {
			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(),
									 WORLDS_DIR + File.separator + String.valueOf(id),
									 String.valueOf(id) + WORLD_FILE_EXTENSION);
			if (AppSettings.useExternalStorageForWorlds() &&
					!fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_worldLoadError);
			}
			stream = new InputStreamReader(new DataInputStream(new BufferedInputStream(
					new FileInputStream(file.getAbsolutePath()))));
			Gson gson = new Gson();
			versionedWorld = gson.fromJson(stream, VersionedWorld.class);

			if(versionedWorld.appVersion > APP_VERSION_ID) {
				throw new DaoException(R.string.exception_versionMismatch);
			}
		}
		catch (IOException ex) {
			throw new DaoException(R.string.exception_worldLoadError, id, ex);
		}
		finally {
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

		return versionedWorld.world;
	}

	@Override
	public Collection<World> load(Collection<DaoFilter> filters) {
		// TODO: implement this method
		return null;
	}

	public String getSaveFileName() {
		return saveFileName;
	}

	@Override
	public boolean save(World aWorld) {
		DataOutputStream stream = null;
		File file = null;

		try {
			saveFileName = aWorld.getName() + WORLD_FILE_EXTENSION;
			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(),
					WORLDS_DIR, saveFileName);
			if(AppSettings.useExternalStorageForWorlds() &&
					!fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_worldLoadError);
			}
			stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			Gson gson = new Gson();
			String json = gson.toJson(aWorld);
			stream.writeChars(json);
			stream.flush();
//			file.getAbsolutePath();
			return true;
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
	 * @param filters  the filters to use to determine which files to delete.
	 */
	public int delete(Collection<DaoFilter> filters) {
		// TODO: implement this method
		return -1;
//		Collection<File> files;
//		String relativePath = WORLDS_DIR + File.separator + aWorld.getName();
//		String fileName = ALL_FILES_REGEX;
//
//		files = fileUtils.getFiles(AppSettings.useExternalStorageForWorlds(),
//								   relativePath, fileName);
//
//		for(File aFile : files) {
//			if(!aFile.delete()) {
//				throw new DaoException(R.string.exception_worldNotRemoved, aWorld.getName());
//			}
//		}
//		if(files.size() > 0) {
//			if(!files.iterator().next().getParentFile().delete()) {
//				throw new DaoException(R.string.exception_worldNotRemoved, aWorld.getName());
//			}
//		}
	}

//	private void readRegion(DataInputStream stream, World parent) {
//		String regionName = "UNKNOWN";
//		Region newRegion;
//
//		try {
//			regionName = stream.readUTF();
//			newRegion = parent.getRegionNameMap().get(regionName);
//			if(newRegion == null) {
//				newRegion = new Region(regionName, parent);
//			}
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(dateFormat.parse(stream.readUTF()));
//			newRegion.setCreateTs(cal);
//			cal.setTime(dateFormat.parse(stream.readUTF()));
//			newRegion.setModifiedTs(cal);
//			newRegion.setWidth(stream.readInt());
//			newRegion.setHeight(stream.readInt());
//			int numCells = stream.readInt();
//			for(int i = 0; i < numCells; i++) {
//				readCell(stream, newRegion, parent);
//			}
//		}
//		catch (IOException | ParseException ex) {
//			throw new DaoException(R.string.exception_regionLoadError, regionName,
//								   parent.getName());
//		}
//	}

//	private void readCell(DataInputStream stream, Region parent, World world) {
//		Cell newCell = new Cell();
//		int x = -1;
//		int y = -1;
//
//		try {
//			newCell.setParent(parent);
//			x = stream.readInt();
//			newCell.setX(x);
//			y = stream.readInt();
//			newCell.setY(y);
//			newCell.setSolid(stream.readBoolean());
//			DaoFilter daoFilter = new DaoFilterJsonImpl(DaoFilter.Operator.EQUALS, WorldDaoSqlImpl.WorldsContract.NAME_COLUMN_NAME,
//					);
//			terrainDao.load()
//			terrainManager.getTerrainWithName(stream.readUTF());
//			parent.putCell(newCell);
//
//			// Read cell links
//			int numLinks = stream.readInt();
//			for(int i = 0; i < numLinks; i++) {
//				@DataConstants.Direction int linkDirection = stream.readInt();
//				String linkRegionName = stream.readUTF();
//				Region linkParent = world.getRegionNameMap().get(linkRegionName);
//				if(linkParent == null) {
//					linkParent = new Region(linkRegionName, world);
//				}
//				int linkX = stream.readInt();
//				int linkY = stream.readInt();
//				Cell linkCell = linkParent.getCell(linkX, linkY);
//				if(linkCell == null) {
//					linkCell = new Cell();
//					linkCell.setX(linkX);
//					linkCell.setY(linkY);
//					linkParent.putCell(linkCell);
//				}
//				newCell.setLinkForDirection(linkDirection, linkCell);
//			}
//
//			// Read cell exits
//			int numExits = stream.readInt();
//			for(int i = 0; i < numExits; i++) {
//				@DataConstants.Direction int exitDirection = stream.readInt();
//				int cellExitId = stream.readInt();
//				CellExitType exitCell = cellExitManager.getCellExitWithId(cellExitId);
//				newCell.setExitForDirection(exitDirection, exitCell);
//			}
//		}
//		catch (IOException ex) {
//			throw new DaoException(R.string.exception_cellLoadError, x, y, parent.getName(),
//								   parent.getParent().getName());
//		}
//	}

//	private void saveRegionToFile(DataOutputStream stream, Region region) {
//		try {
//			stream.writeUTF(region.getName());
//			stream.writeUTF(dateFormat.format(region.getCreateTs().getTime()));
//			stream.writeUTF(dateFormat.format(region.getModifiedTs().getTime()));
//			stream.writeInt(region.getWidth());
//			stream.writeInt(region.getHeight());
//			stream.writeInt(region.getCells().size());
//			for(Cell aCell : region.getCells()) {
//				saveCellToFile(stream, aCell);
//			}
//		}
//		catch (IOException ex) {
//			throw new DaoException(R.string.exception_regionNotSaved, region.getName(),
//								   region.getParent().getName());
//		}
//	}

//	private void saveCellToFile(DataOutputStream stream, Cell cell) {
//		try {
//			stream.writeUTF(cell.getParent().getName());
//			stream.writeInt(cell.getX());
//			stream.writeInt(cell.getY());
//			stream.writeBoolean(cell.isSolid());
//			stream.writeUTF(cell.getTerrain().getName());
//
//			// Save linked cell references
//			stream.writeInt(cell.getNumLinks());
//			for(@DataConstants.Direction int direction = NORTH; direction <= DOWN; direction++) {
//				Cell linkCell = cell.getLinkForDirection(direction);
//				if(linkCell != null) {
//					stream.writeInt(direction);
//					stream.writeUTF(linkCell.getParent().getName());
//					stream.writeInt(linkCell.getX());
//					stream.writeInt(linkCell.getY());
//				}
//			}
//
//			// Save CellExit references
//			stream.writeInt(cell.getNumExits());
//			for(@DataConstants.Direction int direction = NORTH; direction <= DOWN; direction++) {
//				CellExitType exit = cell.getExitForDirection(direction);
//				if(exit != null) {
//					stream.writeInt(direction);
//					stream.writeInt(exit.getId());
//				}
//			}
//		}
//		catch (IOException ex) {
//			throw new DaoException(R.string.exception_cellNotSaved, cell.getX(), cell.getY(),
//								   cell.getParent().getName(),
//								   cell.getParent().getParent().getName());
//		}
//	}

	private class VersionedWorld {
		public long appVersion;
		public World world;
	}
}
