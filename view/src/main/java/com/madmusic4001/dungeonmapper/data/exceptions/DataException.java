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

package com.madmusic4001.dungeonmapper.data.exceptions;

import android.support.annotation.StringRes;

import com.madmusic4001.dungeonmapper.view.exceptions.DungeonMapperException;

/**
 * Exception type to be caught at the UI level and display a message to the user.
 */
public class DataException extends DungeonMapperException {
	/**
	 * Constructs a new {@code DungeonMapperException} that includes the current stack
	 * trace and the specified errorStringResourceId.
	 *
	 * @param errorStringResourceId the string resource id to use as the error message to display
	 *                              in the UI.
	 * @param formatArgs            an optional set of objects to be used to format the resource
	 *                                 string.
	 */
	public DataException(@StringRes int errorStringResourceId, Object... formatArgs) {
		super(errorStringResourceId, formatArgs);
	}

	/**
	 * Constructs a new {@code DungeonMapperException} with the current stack trace,
	 * the specified detail message and the specified cause and the specified
	 * errorStringResourceId.
	 *
	 * @param cause                 the cause of this exception.
	 * @param errorStringResourceId the string resource id to use as the error message to display
	 *                              in the UI.
	 * @param formatArgs            an optional set of objects to be used to format the resource
	 *                                 string.
	 */
	public DataException(Throwable cause, @StringRes int errorStringResourceId,
						 Object... formatArgs) {
		super(cause, errorStringResourceId, formatArgs);
	}
}
