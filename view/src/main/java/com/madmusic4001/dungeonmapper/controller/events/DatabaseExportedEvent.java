/**
 * Copyright (C) 2016 MadInnovations
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
package com.madmusic4001.dungeonmapper.controller.events;

/**
 * Event to notify subscribers of the results of a database export operation.
 */
public class DatabaseExportedEvent {
	private boolean successful;
	private int worldCount;
	private int regionCount;
	private int cellCount;
	private int cellExitTypeCount;
	private int terrainCount;

	/**
	 * Create a new DatabaseExportedEvent instance with the given parameters.
	 *
	 * @param successful  true if the export succeeded, otherwise false
	 * @param worldCount  the number of worlds that were exported
	 * @param regionCount  the number of regions that were exported
	 * @param cellCount  the number of cells that were exported
	 * @param cellExitTypeCount  the number of cell exit types that were exported
	 * @param terrainCount  the number of terrains that were exported
	 */
	public DatabaseExportedEvent(boolean successful, int worldCount, int regionCount, int cellCount, int cellExitTypeCount,
								 int terrainCount) {
		this.successful = successful;
		this.worldCount = worldCount;
		this.regionCount = regionCount;
		this.cellCount = cellCount;
		this.cellExitTypeCount = cellExitTypeCount;
		this.terrainCount = terrainCount;
	}

	// Getter
	public boolean isSuccessful() {
		return successful;
	}
	public int getWorldCount() {
		return worldCount;
	}
	public int getRegionCount() {
		return regionCount;
	}
	public int getCellCount() {
		return cellCount;
	}
	public int getCellExitTypeCount() {
		return cellExitTypeCount;
	}
	public int getTerrainCount() {
		return terrainCount;
	}
}
