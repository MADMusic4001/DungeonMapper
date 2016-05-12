/**
 * Copyright (C) 2014 MadMusic4001
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

package com.madmusic4001.dungeonmapper.view.utils;

import java.io.File;

/**
 * Utility class to maintain constants used in multiple places in the app
 */
public final class StorageConstants {
	public static final long   APP_VERSION_ID         = 20140717001L;
	public static final int    DB_VERSION             = 1;
	public static final String DB_NAME                = "dungeon_mapper_db";
	public static final String PACKAGE_NAME           = "com.madmusic4001.rpgmaker";
	public static final String PROJECTS_DIR           = File.separator + "projects";
	public static final String TERRAIN_DIR            = File.separator + "terrains";
	public static final String WORLD_FILE_EXTENSION   = ".wrl";
	public static final String REGION_FILE_EXTENSION  = ".rgn";
	public static final String REGION_FILES_REGEX     = ".*" + File.pathSeparator
			+ REGION_FILE_EXTENSION;
	public static final String TERRAIN_FILE_EXTENSION = ".trn";
	public static final String TERRAIN_FILES_REGEX    = ".*" + File.pathSeparator +
			TERRAIN_FILE_EXTENSION;
	public static final String ALL_FILES_REGEX        = ".*";
}
