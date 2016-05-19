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

import com.madmusic4001.dungeonmapper.data.entity.Cell;

/**
 * Event notifying subscribers that an instance of T was saved to persistent storage.
 */
public class SavedEvent<T> {
	boolean successful;
	T item;

	/**
	 * Creates a new SavedEvent<T> instance.
	 *
	 * @param successful  true if successful, otherwise false
	 * @param item  the instance of T that was saved
	 */
	public SavedEvent(boolean successful, T item) {
		this.successful = successful;
		this.item = item;
	}

	// Getters
	public boolean isSuccessful() {
		return successful;
	}
	public T getItem() {
		return item;
	}
}
