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
package com.madmusic4001.dungeonmapper.view;

import android.app.Application;
import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.managers.WorldManager;
import com.madmusic4001.dungeonmapper.view.di.components.ApplicationComponent;
import com.madmusic4001.dungeonmapper.view.di.components.DaggerApplicationComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ApplicationModule;

import javax.inject.Inject;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 7/3/2015.
 */
public class DungeonMapperApp extends Application {
	private ApplicationComponent applicationComponent;
	@Inject
	WorldManager worldManager;

	@Override
	public void onCreate() {
		super.onCreate();
		this.initializeInjector();
	}

	private void initializeInjector() {
		this.applicationComponent = DaggerApplicationComponent.builder().applicationModule(
				new ApplicationModule(this)).build();
		Log.d("DungeonMapperApp", "ApplicationComponent successfully created.");
	}

	public ApplicationComponent getApplicationComponent() {
		return this.applicationComponent;
	}
}
