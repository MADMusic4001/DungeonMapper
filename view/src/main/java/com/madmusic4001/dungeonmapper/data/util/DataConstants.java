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

package com.madmusic4001.dungeonmapper.data.util;

import android.support.annotation.IntDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Singleton;

/**
 * Utility class to maintain constants used in multiple places in the app
 */
@Singleton
public final class DataConstants {
	public static final long   APP_VERSION_ID       = 20140717001L;
	public static final String WORLDS_DIR = File.separator + "worlds";
	public static final int    DB_VERSION           = 2;
	public static final int    DB_VERSION_1         = 1;
	public static final String DB_NAME              = "dungeon_mapper_db";
	public static final String PACKAGE_NAME         = "com.madmusic4001.rpgmaker";
	public static final String WORLD_FILE_EXTENSION = ".wrl";
	public static final String ALL_FILES_REGEX      = ".*";
	public static final String CURRENT_WORLD_ID    = PACKAGE_NAME + ".current_world_id";
	public static final String CURRENT_REGION_ID    = PACKAGE_NAME + ".current_region_id";
	public static final int UNINITIALIZED             = -1;

	public static final int ID_WHEN_RESOURCE_NOT_FOUND        = -1;
	public static final int TERRAIN_NAME_ARRAY_RESOURCE_INDEX = 1;

	public static final int CELL_EXIT_ARRAY_RESOURCE_NAME_INDEX = 6;
	public static final int UP    = 0;
	public static final int NORTH = 1;
	public static final int WEST  = 2;
	public static final int EAST  = 3;
	public static final int SOUTH = 4;
	public static final int DOWN  = 5;
	@IntDef({UP, NORTH, WEST, EAST, SOUTH, DOWN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Direction {
	}
	public static final int NUM_EXIT_DIRECTIONS = 6;

	/**
	 * Specifies where the x and y axes cross. This information is used to determine which cells
	 * to preserve when a map size is
	 * shrunk and for coordinate display when the user long presses a map cell.
	 */
	public static final int SOUTHWEST = 0;
	public static final int SOUTHEAST = 1;
	public static final int NORTHWEST = 2;
	public static final int NORTHEAST = 3;
	@IntDef({SOUTHWEST, SOUTHEAST, NORTHWEST, NORTHEAST})
	@Retention(RetentionPolicy.SOURCE)
	public @interface OriginLocation {
	}
}
