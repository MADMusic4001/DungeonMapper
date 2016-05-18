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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.sql.TerrainDaoSqlImpl;
import com.madmusic4001.dungeonmapper.data.entity.AppSettings;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
import com.madmusic4001.dungeonmapper.data.util.BitMapUtils;
import com.madmusic4001.dungeonmapper.data.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.APP_VERSION_ID;

/**
 *
 */
@SuppressWarnings("unused")
//@Singleton
public class TerrainDaoJsonImpl implements TerrainDao {
	public static final String TERRAIN_DIR = File.separator + "terrains";
	public static final String TERRAIN_FILE_EXTENSION = ".trn";
	public static final String TERRAIN_FILES_REGEX = ".*" + File.pathSeparator +
			TERRAIN_FILE_EXTENSION;

//	@Inject
	protected FileUtils fileUtils;

	@Override
	public int count(Collection<DaoFilter> filters) {
		throw new UnsupportedOperationException("Count not implemented for TerrainDaoFileImple.");
	}

	@Override
	public Terrain load(int id) {
		throw new UnsupportedOperationException("Load() not implemented for TerrainDaoFileImple.");
	}

	@Override
	public Collection<Terrain> load(Collection<DaoFilter> filters) {
		if(filters != null && !filters.isEmpty()) {
			throw new UnsupportedOperationException("LoadWithFilter(Terrain filter) not implemented "
															+ "for TerrainDaoFileImple.");
		}
		ArrayList<Terrain> allTerrains = new ArrayList<>();

		Collection<File> terrains = fileUtils.getFiles(AppSettings.useExternalStorageForWorlds(),
													   TERRAIN_DIR,
													   TERRAIN_FILES_REGEX);
		for (File aTerrainFile : terrains) {
			allTerrains.add(readFromFile(aTerrainFile));
		}
		return allTerrains;
	}

	@Override
	public boolean save(Terrain aTerrain) {
		File file = null;
		DataOutputStream stream = null;

		try {
			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(),
									 TERRAIN_DIR,
									 aTerrain.getName() + TERRAIN_FILE_EXTENSION);
			if (AppSettings.useExternalStorageForWorlds() &&
					!fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_terrainLoadError);
			}
			stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			VersionedTerrain versionedTerrain = new VersionedTerrain();
			versionedTerrain.versionNbr = APP_VERSION_ID;
			versionedTerrain.terrain = aTerrain;
			versionedTerrain.imageString = BitMapUtils.getStringFromBitmap(aTerrain.getImage());
			Gson gson = new Gson();
			gson.toJson(versionedTerrain);
			ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
			aTerrain.getImage().compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
			byte[] bytes = bitmapStream.toByteArray();
			stream.writeInt(bytes.length);
			stream.write(bytes);
			stream.flush();
		}
		catch (FileNotFoundException ex) {
			Log.e(this.getClass().getName(), file.getAbsolutePath() +
					" could not be opened for writing", ex);
			throw new DaoException(R.string.exception_terrainNotSaved, file.getAbsolutePath(), ex);
		}
		catch(IOException ex) {
			Log.e(this.getClass().getName(), "Error occurred while writing " +
					file.getAbsolutePath(), ex);
			throw new DaoException(R.string.exception_terrainNotSaved, file.getAbsolutePath(), ex);
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
		return true;
	}

	@Override
	public int delete(Collection<DaoFilter> filters) {
		int result;
		if(filters == null || filters.isEmpty()) {
			result = deleteAll();
		}
		else {
			if(filters.size() > 1) {
				throw new UnsupportedOperationException("TerrainDaoJsonImpl.delete() can only accept 0 or 1 filter");
			}
			DaoFilter filter = filters.iterator().next();
			if(!filter.getFieldName().equals(TerrainDaoSqlImpl.TerrainsContract.COLUMN_NAME_NAME)) {
				throw new UnsupportedOperationException("TerrainDaoJsonImpl.delete() filter must be on NAME field");
			}
			File file;
			String relativePath = TERRAIN_DIR;
			String fileName = filter.getValue().concat(TERRAIN_FILE_EXTENSION);

			file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(), relativePath, fileName);

			result = (file.delete() ? 1 : 0);
		}
		return result;
	}

	private int deleteAll() {
		int result = 0;
		File root = new File(TERRAIN_DIR);
		File[] files = root.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.delete()) {
					result ++;
				}
			}
		}
		return result;
	}

	private Terrain readFromFile(File file) {
		Bitmap bitmap;
		InputStreamReader stream = null;
		VersionedTerrain versionedTerrain = null;
		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			if(AppSettings.useExternalStorageForWorlds() &&
                    !fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_terrainLoadError);
			}
			stream = new InputStreamReader(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
			Gson gson = new Gson();
			versionedTerrain = gson.fromJson(stream, VersionedTerrain.class);
			if(versionedTerrain.versionNbr > APP_VERSION_ID) {
				throw new DaoException(R.string.exception_versionMismatch);
			}
			versionedTerrain.terrain.setImage(BitMapUtils.getBitmapFromString(versionedTerrain.imageString));
		}
		catch (JsonSyntaxException | JsonIOException ex) {
			Log.e(this.getClass().getName(), "Error reading " + file.getAbsolutePath(), ex);
			throw new DaoException(R.string.exception_terrainLoadError, file.getAbsolutePath(), ex);
		}
		catch(FileNotFoundException ex) {
			Log.e(this.getClass().getName(), file.getAbsolutePath() + " not found.", ex);
			throw new DaoException(R.string.exception_terrainLoadError, file.getAbsolutePath(), ex);
		}
		finally {
			if(stream != null) {
				try {
					stream.close();
				}
				catch (IOException ex) {
					Log.e(this.getClass().getName(), "Error closing stream.", ex);
				}
			}
		}
		return versionedTerrain.terrain;
	}

	private class VersionedTerrain {
		public long versionNbr;
		public Terrain terrain;
		public String imageString;
	}
}
