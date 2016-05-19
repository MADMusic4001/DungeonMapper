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

import com.madmusic4001.dungeonmapper.controller.eventhandlers.CellEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.WorldEventHandler;
import com.madmusic4001.dungeonmapper.view.di.components.ApplicationComponent;
import com.madmusic4001.dungeonmapper.view.di.components.DaggerApplicationComponent;
import com.madmusic4001.dungeonmapper.view.di.modules.ApplicationModule;
import com.madmusic4001.dungeonmapper.view.di.modules.EventHandlerModule;

import javax.inject.Inject;

/**
 * Main class for maintaining application state.
 */
public class DungeonMapperApp extends Application {
	private ApplicationComponent applicationComponent;
	@Inject
	protected CellEventHandler cellEventHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		this.initializeInjector();
	}

	private void initializeInjector() {
		this.applicationComponent = DaggerApplicationComponent.builder()
				.eventHandlerModule(new EventHandlerModule())
				.applicationModule(new ApplicationModule(this))
				.build();
	}

	public ApplicationComponent getApplicationComponent() {
		return this.applicationComponent;
	}
}
