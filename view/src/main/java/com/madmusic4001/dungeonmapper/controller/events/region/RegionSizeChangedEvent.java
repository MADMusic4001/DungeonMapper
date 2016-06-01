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
package com.madmusic4001.dungeonmapper.controller.events.region;

import com.madmusic4001.dungeonmapper.data.entity.World;

/**
 * Event representing the results of changing the height and/or width of regions in this world.
 */
public class RegionSizeChangedEvent {
	private int width;
	private int height;

	/**
	 * Creates a new RegionSizeChangedEvent instance.
	 *
	 * @param height  the new region height
	 * @param width  the new region width;
	 */
	public RegionSizeChangedEvent(int height, int width) {
		this.height = height;
		this.width = width;
		if(width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Width and height must be > 0");
		}
	}

	// Getters
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
