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

package com.madmusic4001.dungeonmapper.data.entity;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.data.util.DataConstants;

/**
 * Map cell exit style
 */
public class CellExit {
    private int id = -1;
	private String name;
	private boolean solid;
	private boolean	userCreated;
	private SparseArray<Bitmap> directionsBitmapsMap = new SparseArray<Bitmap>();

    /**
     * Creates a new instance.
     */
    public CellExit() { }

    /**
     * Gets the {@link Bitmap} for the given direction.
     *
     * @param direction the desired {@link DataConstants.Direction} for the {@link Bitmap}.
     * @return a Bitmap or {@code null} if not found.
     */
	public Bitmap getBitmapForDirection(@DataConstants.Direction int direction) {
		return directionsBitmapsMap.get(direction);
	}

	/**
	 * Add a {@link Bitmap} for the given direction.
	 * 
	 * @param direction the desired {@link DataConstants.Direction}.
	 * @param bitmap the {@link Bitmap} to add.
	 */
	public void addBitmapForDirection(@DataConstants.Direction int direction, Bitmap bitmap) {
		directionsBitmapsMap.put(direction, bitmap);
	}

    public SparseArray<Bitmap> getDirectionsBitmapsMap() {
        return directionsBitmapsMap;
    }
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
    public void setName(String name) {
		this.name = name;
	}
	public boolean isSolid() {
		return solid;
	}
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	public boolean isUserCreated() {
		return userCreated;
	}
	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}
}
