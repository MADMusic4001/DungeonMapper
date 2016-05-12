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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class AppSettings implements Externalizable {
	private static final long serialVersionUID = 0L;

	private static boolean externalStorageForUserTerrains = true;
	private static boolean externalStorageForWorlds = true;
	private static boolean externalStorageForSettings = false;

	/**
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput input) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		// TODO Auto-generated method stub

	}

	public static boolean useExternalStorageForUserTerrains() {
		return externalStorageForUserTerrains;
	}
	public static void setExternalStorageForUserTerrains(boolean documentsDirForUserTerrains) {
		AppSettings.externalStorageForUserTerrains = documentsDirForUserTerrains;
	}
	public static boolean useExternalStorageForWorlds() {
		return externalStorageForWorlds;
	}
	public static void setExternalStorageForWorlds(boolean documentsDirForWorlds) {
		AppSettings.externalStorageForWorlds = documentsDirForWorlds;
	}
	public static boolean useExternalStorageForSettings() {
		return externalStorageForSettings;
	}
	public static void setExternalStorageForSettings(boolean documentsDirForSettings) {
		AppSettings.externalStorageForSettings = documentsDirForSettings;
	}
}
