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
package com.madmusic4001.dungeonmapper.controller.events;

import android.content.Context;
import android.os.AsyncTask;

import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
import com.madmusic4001.dungeonmapper.data.entity.Region;

import javax.inject.Inject;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/3/2015.
 */
public class SaveRegionEvent extends EventsProcessor<SaveRegionEvent> {
	@Inject
	WorldManager manager;
	private Region region;

	/**
	 * Constructor for creating an instance of this class to be injected into other classes by
	 * Dagger 2.
	 */
	@Inject
	public SaveRegionEvent(Context context) {
		super(context);
	}

	@Override
	public void processEvent(EventsVisitor visitor, Object... params) {
		super.processEvent(visitor, params);

		if (params.length < 1) {
			throw new IllegalArgumentException("At least 1 value must be passed in the params "
													   + "argument.");
		}
		try {
			region = (Region) params[0];
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("params[0] must be an instance of the Region to be"
													   + " saved.", ex);
		}
	}

	public Region getRegion() {
		return region;
	}
}
