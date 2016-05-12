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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.AppSettings;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.madmusic4001.dungeonmapper.data.util.DataConstants.APP_VERSION_ID;

/**
 *
 */
@SuppressWarnings("unused")
@Singleton
public class TerrainDaoFileImpl implements TerrainDao {
	public static final String TERRAIN_DIR = File.separator + "terrains";
	public static final String TERRAIN_FILE_EXTENSION = ".trn";
	public static final String TERRAIN_FILES_REGEX = ".*" + File.pathSeparator +
			TERRAIN_FILE_EXTENSION;

	@Inject
	protected FileUtils fileUtils;

	@Override
	public int count() {
		throw new UnsupportedOperationException("Count not implemented for TerrainDaoFileImple.");
	}

	@Override
	public Terrain load(String id) {
		throw new UnsupportedOperationException("Load() not implemented for TerrainDaoFileImple.");
	}

	@Override
	public Collection<Terrain> loadWithFilter(Terrain filter) {
		throw new UnsupportedOperationException("LoadWithFilter(Terrain filter) not implemented "
														+ "for TerrainDaoFileImple.");
	}

	@Override
	public Collection<Terrain> loadAll() {
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
	public void save(Terrain aTerrain) {
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
			stream.writeLong(APP_VERSION_ID);
			stream.writeUTF(aTerrain.getName());
			stream.writeBoolean(aTerrain.isUserCreated());
			stream.writeBoolean(aTerrain.isSolid());
			stream.writeBoolean(aTerrain.canConnect());
			Map<String, String> displayNames = aTerrain.getLocaleDisplayNames();
			stream.writeInt(displayNames.size());
			for(Map.Entry<String, String> entry : displayNames.entrySet()) {
				stream.writeUTF(entry.getKey());
				stream.writeUTF(entry.getValue());
			}
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
	}

	@Override
	public void delete(Terrain aTerrain) {
		File file;
		String relativePath = TERRAIN_DIR;
		String fileName = aTerrain.getName().concat(TERRAIN_FILE_EXTENSION);

		file = fileUtils.getFile(AppSettings.useExternalStorageForWorlds(), relativePath, fileName);

		if(!file.delete()) {
			throw new DaoException(R.string.exception_terrainNotRemoved);
		}
	}

	private Terrain readFromFile(File file) {
		Bitmap bitmap;
		DataInputStream stream = null;
		Terrain terrain = new Terrain("");
		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			if(AppSettings.useExternalStorageForWorlds() &&
                    !fileUtils.isExternalStorageReadable()) {
				Log.e(this.getClass().getName(), "External storage unavailable");
				throw new DaoException(R.string.exception_terrainLoadError);
			}
			stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			long appVersion = stream.readLong();
			if(appVersion > APP_VERSION_ID) {
				throw new DaoException(R.string.exception_versionMismatch);
			}
			terrain.setName(stream.readUTF());
			terrain.setUserCreated(stream.readBoolean());
			terrain.setSolid(stream.readBoolean());
			terrain.setConnect(stream.readBoolean());
			int numDisplayNames = stream.readInt();
			for(int i = 0; i < numDisplayNames; i++) {
				terrain.addDisplayName(stream.readUTF(), stream.readUTF());
			}
            byte[] bitmapData = new byte[stream.readInt()];
            int bitmapLengthRead = stream.read(bitmapData);
            options.inScaled = false;
            bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapLengthRead, options);
            terrain.setImage(bitmap);
		}
		catch(FileNotFoundException ex) {
			Log.e(this.getClass().getName(), file.getAbsolutePath() + " not found.", ex);
			throw new DaoException(R.string.exception_terrainLoadError, file.getAbsolutePath(), ex);
		}
		catch(IOException ex) {
			Log.e(this.getClass().getName(), "Error reading " + file.getAbsolutePath(), ex);
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
		return terrain;
	}
}
