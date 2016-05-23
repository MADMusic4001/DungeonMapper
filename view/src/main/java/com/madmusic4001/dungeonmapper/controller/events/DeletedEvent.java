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

import java.util.Collection;

/**
 * Event representing the results of a request to delete 1 or more instances of T.
 */
public abstract class DeletedEvent<T> {
	boolean successful;
	int     numDeleted;
	Collection<T> deleted;

	/**
	 * Creates a DeletedEvent<T> instance.
	 *
	 * @param successful  true, if the cell(s) were deleted
	 * @param numDeleted  the number of instance of T that were deleted
	 * @param deleted  the T instances that were deleted
	 */
	public DeletedEvent(boolean successful, int numDeleted, Collection<T> deleted) {
		this.successful = successful;
		this.numDeleted = numDeleted;
		this.deleted = deleted;
	}

	// Getters
	public boolean isSuccessful() {
		return successful;
	}
	public int getNumDeleted() {
		return numDeleted;
	}
	public Collection<T> getDeleted() {
		return deleted;
	}
}
