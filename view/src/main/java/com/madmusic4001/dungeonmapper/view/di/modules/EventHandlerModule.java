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
package com.madmusic4001.dungeonmapper.view.di.modules;

import com.madmusic4001.dungeonmapper.controller.eventhandlers.CellEventHandler;
import com.madmusic4001.dungeonmapper.controller.eventhandlers.WorldEventHandler;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.FilterCreator;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides instances of event handler classes for dependency injection.
 */
@Module(includes = ApplicationModule.class)
public class EventHandlerModule {
	@Provides @Singleton
	CellEventHandler provideCellEventHandler(EventBus eventBus, CellDao cellDao, FilterCreator filterCreator) {
		CellEventHandler handler = new CellEventHandler(eventBus, cellDao, filterCreator);
		eventBus.register(handler);
		return handler;
	}

	@Provides @Singleton
	WorldEventHandler provideWorldEventHandler(EventBus eventBus, WorldDao worldDao, FilterCreator filterCreator) {
		WorldEventHandler handler = new WorldEventHandler(eventBus, worldDao, filterCreator);
		eventBus.register(handler);
		return handler;
	}
}
