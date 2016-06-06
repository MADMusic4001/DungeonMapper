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

import android.support.annotation.NonNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Manages information about a map
 */
public class Region {
	private             int id    = -1;
	private String		name;
	private World		parent;
	private Calendar	createTs   = Calendar.getInstance();
	private Calendar	modifiedTs = Calendar.getInstance();
	private int			width;
	private int			height;
	private List<Cell>	cells;

	/**
	 * Create a new instance with the given grid size
	 *
	 * @param name the name of this instance.
	 */
	public Region(String name, World world) {
		this.name = name;
		this.parent = world;
		if(world != null) {
			this.width = world.getRegionWidth();
			this.height = world.getRegionHeight();
		}
	}

	/**
	 * Resize the region
	 */
	public void resizeRegion() {
		int newWidth = parent.getRegionWidth();
		int newHeight = parent.getRegionHeight();

		if (width > newWidth || height > newHeight) {
			for (Cell cell : cells) {
				if (cell.getX() >= newWidth || cell.getY() >= newHeight) {
					cells.remove(cell);
				}
			}
		}
	}

	/**
	 * Gets the {@link Cell} at the given x,y position in the cell array.
	 *
	 * @param x the x coordinate for the cell.
	 * @param y the y coordinate for the cell.
	 * @return the desired {@link Cell} or null if the coordinates are out of bounds or if no
	 * {@link Cell} exists at the given coordinates.
	 */
	public Cell getCell(int x, int y) {
		Cell cell = null;
		for (Cell aCell : cells) {
			if (aCell.getX() == x && aCell.getY() == y) {
				cell = aCell;
				break;
			}
		}
		return cell;
	}

	/**
	 * Sets the {@link Cell} at the given position.
	 *
	 * @param newCell the new {@link Cell} to add to the region.
	 */
	public void putCell(@NonNull Cell newCell) {
		for (Cell cell : cells) {
			if (cell.getX() == newCell.getX() && cell.getY() == newCell.getY()) {
				cells.remove(cell);
				break;
			}
		}
		cells.add(newCell);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("id", id)
				.append("name", name)
				.append("parent.id", parent.getId())
				.append("parent.name", parent.getName())
				.append("createTs", createTs)
				.append("modifiedTs", modifiedTs)
				.append("width", width)
				.append("height", height)
				.append("cells", cells)
				.toString();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public World getParent() {
		return parent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Calendar getCreateTs() {
		return createTs;
	}
	public void setCreateTs(Calendar createTs) {
		this.createTs = createTs;
	}
	public Calendar getModifiedTs() {
		return modifiedTs;
	}
	public void setModifiedTs(Calendar modifiedTs) {
		this.modifiedTs = modifiedTs;
	}
    public int getWidth() {
        return width;
    }
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
        return height;
    }
	public void setHeight(int height) {
		this.height = height;
	}
    public List<Cell> getCells() {
		if(cells == null) {
			cells = new ArrayList<>(width * height);
		}
        return cells;
    }
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
}
