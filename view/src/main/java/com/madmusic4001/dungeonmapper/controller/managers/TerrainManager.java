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

package com.madmusic4001.dungeonmapper.controller.managers;

import android.content.Context;
import android.graphics.Bitmap;

import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.util.BitmapPacker;
import com.madmusic4001.dungeonmapper.data.util.PackedBitmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class TerrainManager {
    private static final int BITMAP_SIZE  = 144;
    private static final int PADDING_SIZE = 6;
    private final Context    context;
    private final TerrainDao dao;
    private Map<String, Terrain> terrainNameMap = new HashMap<String, Terrain>();
    private PackedBitmap packedBitmap;
    private BitmapPacker packer;
	private int bitmapSize = BITMAP_SIZE;

    /**
     * Creates a new {@code TerrainManager} instance. This should only be called by Dagger DI
     * framework.
     *
     * @param context the application {@link android.content.Context} instance.
     * @param dao a {@link TerrainDao} instance.
     */
    @Inject
    public TerrainManager(final Context context, final TerrainDao dao) {
        this.context = context;
        this.dao = dao;
    }

    /**
     * Gets the Terrain instance with the specified name.
     *
     * @param terrainName the name of the desired Terrain.
     * @return a Terrain instance or null if not found.
     */
    public Terrain getTerrainWithName(String terrainName) {
        return terrainNameMap.get(terrainName);
    }

    /**
     * Gets the Terrain instance with the specified ID.
     *
     * @param terrainId the ID of the desired Terrain.
     * @return a Terrain instance or null if not found.
     */
    public Terrain getTerrainWithId(long terrainId) {
        Terrain theTerrain = null;
        for (Terrain aTerrain : terrainNameMap.values()) {
			if(aTerrain.getId() == terrainId) {
				theTerrain = aTerrain;
				break;
			}
		}
		return theTerrain;
	}

    /**
     * Loads all {@link Terrain} instances from storage.
     *
     * @return a Collection of Terrain instances.
     */
    public Collection<Terrain> getTerrains() {
		Collection<Terrain> terrains = terrainNameMap.values();
        if(terrains.isEmpty()) {
			terrains = dao.load(null);
			for(Terrain aTerrain : terrains) {
				if(aTerrain.getImage().getWidth() > bitmapSize) {
					bitmapSize = aTerrain.getImage().getWidth();
				}
				terrainNameMap.put(aTerrain.getName(), aTerrain);
			}
        }

		return terrains;
    }

    /**
     * Saves a {@link Terrain} instance.
     *
     * @param terrain the {@link Terrain} instance to be saved.
	 * @return the {@link Terrain} instance that was saved.
     */
    public Terrain saveTerrain(Terrain terrain) {
		dao.save(terrain);
		return terrain;
    }

	// <editor-fold desc="Public actions">

	/**
	 * Loads all {@link Terrain} instances.
	 *
	 * @return a {@link Collection} of {@link Terrain} instances.
	 */
	public Collection<Terrain> loadTerrains() {
		Collection<Terrain> terrains = terrainNameMap.values();
		if (terrains.isEmpty()) {
			terrains = dao.load(null);
			for(Terrain aTerrain : terrains) {
				if(aTerrain.getImage().getWidth() > bitmapSize) {
					bitmapSize = aTerrain.getImage().getWidth();
				}
				terrainNameMap.put(aTerrain.getName(), aTerrain);
			}
		}
		return terrains;
	}

	/**
	 * Gets an initialized {@code BitmapPacker} for use by OpenGL.
	 *
	 * @return a {@link BitmapPacker}
	 */
	public BitmapPacker getBitmapPacker() {
		if(packer == null) {
			packer = createPacker();
			packBitmaps(packer);
		}
		return packer;
	}

	/**
	 * Disposes the TextureAtlas so that it will release its resources.
	 */
	public void recyclePackedBitmap() {
		if(packedBitmap != null) {
			packedBitmap.dispose();
			packedBitmap = null;
		}
	}
	// </editor-fold>

	// <editor-fold desc="Private plumbing methods">
	private BitmapPacker createPacker() {
		// Estimate atlas size based on number of terrains
		int numTerrains = getTerrains().size();
		int width = (int)Math.ceil(Math.sqrt(numTerrains));
		// Calculate how many pixels would be needed to put all bitmaps in row
		// then take the square root to get the size to make it a square texture atlas
		int rowLength = width * (bitmapSize + PADDING_SIZE * 2 + 2);
		// Make the row length as a power of 2
		rowLength--;
		rowLength |= rowLength >> 1;
		rowLength |= rowLength >> 2;
		rowLength |= rowLength >> 4;
		rowLength |= rowLength >> 8;
		rowLength |= rowLength >> 16;
		rowLength++;
		return new BitmapPacker(rowLength, rowLength, PADDING_SIZE, true);
	}

	private void packBitmaps(BitmapPacker packer) {
		for(Terrain terrain : terrainNameMap.values()) {
			Bitmap bitmap = terrain.getImage();
			packer.addImage(terrain.getName(), bitmap);
		}
		packer.pack();
	}
	// </editor-fold>
}
