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

import android.util.SparseArray;

import com.madmusic4001.dungeonmapper.data.util.DataConstants;

/**
 * Manages information about a map cell
 */
public class Cell {
    private int                  	id = -1;
    private Region                  parent = null;
	private int						x;
	private int						y;
    private boolean              	solid = false;
	private Terrain              	terrain = null;
    private SparseArray<Cell>	    directionLinksArray = new SparseArray<>(6);
    private SparseArray<CellExit>   directionExitsArray = new SparseArray<>(6);

    /**
     * Gets the {@code Cell} for the given direction.
     *
     * @param direction  a {@link DataConstants.Direction}.
     * @return the {@link Cell} for the given direction or null if the cell cannot be exited in
     * that direction.
     */
    public Cell getLinkForDirection(@DataConstants.Direction int direction) {
        return directionLinksArray.get(direction);
    }

	/**
	 * Gets the number of other cells that this {@code Cell} links to.
	 *
	 * @return the number of {@link Cell} instances linked to this {@code Cell}
	 */
	public int getNumLinks() {
		return directionLinksArray.size();
	}

    /**
     * Sets the {@code Cell} for the given direction.
     *
     * @param direction  the {@link DataConstants.Direction} for the link to be set.
     * @param link  the {@link Cell} to be set.
     */
    public void setLinkForDirection(@DataConstants.Direction int direction, Cell link) {
        directionLinksArray.put(direction, link);
    }

    /**
     * Gets the {@code CellExit} for the given direction.
     *
     * @param direction  the {@link DataConstants.Direction} of the {@link CellExit}
     * @return the {@link CellExit} for the given direction or null if not found.
     */
    public CellExit getExitForDirection(@DataConstants.Direction int direction) {
        return directionExitsArray.get(direction);
    }

	/**
	 * Gets the number of {@code CellExit} instances that this {@code Cell} uses.
	 *
	 * @return the number of {@link CellExit} instances used by this {@link Cell}
	 */
	public int getNumExits() {
		return directionExitsArray.size();
	}

	/**
     * Sets the {@code CellExit} for the given direction.
     *
     * @param direction  a {@link DataConstants.Direction} for the exit to be set.
     * @param exit  the {@link CellExit} to be set.
     */
    public void setExitForDirection(@DataConstants.Direction int direction, CellExit exit) {
        directionExitsArray.put(direction, exit);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
	public Region getParent() {
		return parent;
	}
	public void setParent(Region parent) {
		this.parent = parent;
	}
	public Terrain getTerrain() {
		return terrain;
	}
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	public boolean isSolid() {
		return solid;
	}
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}
