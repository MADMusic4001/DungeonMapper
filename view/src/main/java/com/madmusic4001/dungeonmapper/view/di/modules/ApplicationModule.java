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
package com.madmusic4001.dungeonmapper.view.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.madmusic4001.dungeonmapper.view.DungeonMapperApp;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides application level class instances for dependency injection.
 */
@Module
public class ApplicationModule {
	static final String PREFS_DEFAULT = "dmapp";
	private final DungeonMapperApp application;

	/**
	 * Creates a new ApplicationModule instance.
	 *
	 * @param application  a {@link DungeonMapperApp} instance.
	 */
	@Inject
	public ApplicationModule(DungeonMapperApp application) {
		this.application = application;
	}

	/**
	 * The {@link Context} for the application.
	 *
	 * @return a Context instance.
	 */
	@Provides @Singleton
	Context provideApplicationContext() {
		return this.application;
	}

	/**
	 * The {@link Application} instance.
	 *
	 * @param app the {@link DungeonMapperApp}.
	 * @return an Application instance
	 */
	@Provides @Singleton
	Application provideApplication(DungeonMapperApp app) {
		return app;
	}

	/**
	 * The {@link DungeonMapperApp}.
	 *
	 * @return a DungeonMapperApp instance.
	 */
	@Provides @Singleton
	DungeonMapperApp provideDungeonMapperApp() {
		return application;
	}

	/**
	 * The {@link SharedPreferences} for the application.
	 *
	 * @param app the {@link Application} whose {@link SharedPreferences} is being requested.
	 * @return a SharedPreferences instance.
	 */
	@Provides @Singleton
	SharedPreferences provideSharedPrefs(Application app) {
		return app.getSharedPreferences(PREFS_DEFAULT, Context.MODE_PRIVATE);
	}

	/**
	 * The {@link EventBus} for the application.
	 *
	 * @return an EventBus instance.
	 */
	@Provides @Singleton
	EventBus providesEventBus() {
		return new EventBus();
	}
}
