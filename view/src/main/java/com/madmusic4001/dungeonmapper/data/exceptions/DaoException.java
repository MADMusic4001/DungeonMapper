/**
 * Copyright (C) 2015 MadMusic4001
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
package com.madmusic4001.dungeonmapper.data.exceptions;

import android.support.annotation.StringRes;

import com.madmusic4001.dungeonmapper.view.exceptions.DungeonMapperException;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/2/2015.
 */
public class DaoException extends DungeonMapperException {
	/**
	 * Constructs a new {@code DungeonMapperException} that includes the current stack
	 * trace and the specified errorStringResourceId.
	 *
	 * @param errorStringResourceId the string resource id to use as the error message to display
	 *                              in the UI.
	 * @param formatArgs            an optional set of objects to be used to format the resource
	 *                                 string.
	 */
	public DaoException(@StringRes int errorStringResourceId, Object... formatArgs) {
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
	public DaoException(Throwable cause, @StringRes int errorStringResourceId,
						Object... formatArgs) {
		super(cause, errorStringResourceId, formatArgs);
	}
}
