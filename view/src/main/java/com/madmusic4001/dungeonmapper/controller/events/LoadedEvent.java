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
 * Event representing the results of a request to load Cell instances from persistent storage.
 */
public class LoadedEvent<T> {
	boolean successful;
	Collection<T> items;

	/**
	 * Creates a LoadedEvent<T> instance.
	 *
	 * @param successful  true if the load operation was successful, otherwise false.
	 * @param items  the collection of items that were loaded
	 */
	public LoadedEvent(boolean successful, Collection<T> items) {
		this.successful = successful;
		this.items = items;
	}

	// Getters
	public boolean isSuccessful() {
		return successful;
	}
	public Collection<T> getItems() {
		return items;
	}
}
