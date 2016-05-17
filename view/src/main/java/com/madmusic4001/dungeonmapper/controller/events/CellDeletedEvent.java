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
 * Event representing the results of a request to delete 1 or more cell instances.
 */
public class CellDeletedEvent {
	boolean success;
	int numDeleted;

	/**
	 * Creates a CellDeletedEvent instance.
	 *
	 * @param success  true, if the cell(s) were deleted
	 * @param numDeleted  the number of cells that were deleted
	 */
	public CellDeletedEvent(boolean success, int numDeleted) {
		this.success = success;
		this.numDeleted = numDeleted;
	}

	// Getters
	public boolean isSuccess() {
		return success;
	}
	public int getNumDeleted() {
		return numDeleted;
	}
}
